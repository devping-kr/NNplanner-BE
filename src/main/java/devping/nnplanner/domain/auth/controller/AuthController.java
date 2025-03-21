package devping.nnplanner.domain.auth.controller;

import devping.nnplanner.domain.auth.dto.request.AuthSignRequestDTO;
import devping.nnplanner.domain.auth.dto.request.EmailCodeRequestDTO;
import devping.nnplanner.domain.auth.dto.request.EmailRequestDTO;
import devping.nnplanner.domain.auth.dto.request.GoogleLoginRequestDTO;
import devping.nnplanner.domain.auth.dto.response.AuthResponseDTO;
import devping.nnplanner.domain.auth.dto.response.AuthTokenResponseDTO;
import devping.nnplanner.domain.auth.service.AuthService;
import devping.nnplanner.domain.auth.service.EmailService;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auths")
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(
        @RequestBody @Valid AuthSignRequestDTO authSignRequestDTO) {

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
        @RequestBody @Valid EmailCodeRequestDTO emailCodeRequestDTO) {

        boolean isVerified = emailService.verifyEmail(emailCodeRequestDTO);

        if (isVerified) {
            return GlobalResponse.OK("이메일 인증 성공", null);
        } else {
            return GlobalResponse.BAD_REQUEST("이메일 인증 실패", null);
        }
    }

    @GetMapping("/reissue")
    public ResponseEntity<ApiResponse<AuthTokenResponseDTO>> reissueToken(
        @RequestHeader("Refreshtoken") String refreshToken) {

        AuthTokenResponseDTO authTokenResponseDTO = authService.reissueToken(refreshToken);

        return GlobalResponse.OK("JWT 토큰 재발급 성공", authTokenResponseDTO);
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        HttpServletRequest httpRequest,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        authService.logout(httpRequest, userDetails);

        return GlobalResponse.OK("유저 로그아웃 성공", null);
    }

    @PostMapping("/oauth2/google-url")
    public ResponseEntity<ApiResponse<String>> loginUrlGoogle() {
        return GlobalResponse.OK("구글 로그인 url 리턴 성공", authService.loginUrlGoogle());
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> loginGoogle(
        @RequestBody @Valid GoogleLoginRequestDTO googleLoginRequestDTO) {

        AuthResponseDTO authResponseDTO = authService.loginGoogle(googleLoginRequestDTO);

        return GlobalResponse.OK("구글 로그인 성공", authResponseDTO);
    }
}
