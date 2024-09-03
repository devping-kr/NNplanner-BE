package devping.nnplanner.domain.user.service;

import devping.nnplanner.domain.user.dto.request.UserRequestDTO;
import devping.nnplanner.domain.user.entity.User;
import devping.nnplanner.domain.user.repository.UserRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;

    public void signUp(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL);
        }
        ;
        if (!userRequestDTO.getPassword().equals(userRequestDTO.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }
        ;

        User user = new User();

        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setCreatedBy(userRequestDTO.getEmail());

        userRepository.save(user);
    }

}
