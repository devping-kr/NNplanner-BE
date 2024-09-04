package devping.nnplanner.domain.auth.controller;

import devping.nnplanner.domain.auth.dto.request.AuthSignRequestDTO;
import devping.nnplanner.domain.auth.dto.request.EmailCodeRequestDTO;
import devping.nnplanner.domain.auth.dto.request.EmailRequestDTO;
import devping.nnplanner.domain.auth.service.AuthService;
import devping.nnplanner.domain.auth.service.EmailService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auths")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(
        @RequestBody AuthSignRequestDTO authSignRequestDTO) {

        authService.signUp(authSignRequestDTO);

        return GlobalResponse.CREATED("유저 회원가입 성공", null);
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendEmail(
        @RequestBody @Valid EmailRequestDTO emailRequestDTO)
        throws MessagingException {

        emailService.sendEmail(emailRequestDTO);

        return GlobalResponse.OK("인증번호 발송 성공", null);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
        @RequestBody EmailCodeRequestDTO emailCodeRequestDTO) {

        boolean isVerified = emailService.verifyEmail(emailCodeRequestDTO);

        if (isVerified) {
            return GlobalResponse.OK("이메일 인증 성공", null);
        } else {
            return GlobalResponse.BAD_REQUEST("이메일 인증 실패", null);
        }
    }
}
