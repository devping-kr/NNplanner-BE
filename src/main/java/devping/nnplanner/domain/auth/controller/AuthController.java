package devping.nnplanner.domain.auth.controller;

import devping.nnplanner.domain.auth.dto.request.AuthRequestDTO;
import devping.nnplanner.domain.auth.service.AuthService;
import devping.nnplanner.domain.auth.service.EmailService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.ResponseUtil;
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
        @RequestBody AuthRequestDTO authRequestDTO) {

        authService.signUp(authRequestDTO);

        return ResponseUtil.CREATED(ApiResponse.of("유저 회원가입 성공", null));
    }
}
