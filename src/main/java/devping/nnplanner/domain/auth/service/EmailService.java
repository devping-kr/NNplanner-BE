package devping.nnplanner.domain.auth.service;

import devping.nnplanner.domain.auth.dto.request.EmailCodeRequestDTO;
import devping.nnplanner.domain.auth.dto.request.EmailRequestDTO;
import devping.nnplanner.domain.auth.entity.Email;
import devping.nnplanner.domain.auth.repository.EmailRepository;
import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private JavaMailSender mailSender;

    @Autowired
    public EmailService(UserRepository userRepository, EmailRepository emailRepository,
                        JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendEmail(EmailRequestDTO emailRequestDTO) throws MessagingException {

        if (userRepository.existsByEmail(emailRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL);
        }
        ;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String verificationCode = verificationCode();

        helper.setTo(emailRequestDTO.getEmail());
        helper.setSubject("냠냠플래너 인증번호 입니다.");
        helper.setText("인증번호는 " + verificationCode + " 입니다.", true);

        mailSender.send(message);

        if (emailRepository.existsByEmail(emailRequestDTO.getEmail())) {

            Email email = emailRepository.findByEmail(emailRequestDTO.getEmail())
                                         .orElseThrow(
                                             () -> new CustomException(ErrorCode.NOT_FOUND));

            email.update(verificationCode, Instant.now().toEpochMilli() + 30000);

            emailRepository.save(email);
        } else {

            Email email = new Email();

            email.create(
                emailRequestDTO.getEmail(),
                verificationCode,
                Instant.now().toEpochMilli() + 30000,
                false,
                Instant.now());

            emailRepository.save(email);
        }
    }

    @Transactional
    public boolean verifyEmail(EmailCodeRequestDTO emailCodeRequestDTO) {

        Email email = emailRepository.findByEmail(emailCodeRequestDTO.getEmail())
                                     .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (email.getVerificationCode().equals(emailCodeRequestDTO.getVerifyCode()) &&
            email.getExpirationTime() > Instant.now().toEpochMilli()) {

            email.verify(true);
            emailRepository.save(email);

            return true;
        }

        return false;
    }

    private String verificationCode() {

        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 랜덤 숫자 생성
        return String.valueOf(code);
    }
}
