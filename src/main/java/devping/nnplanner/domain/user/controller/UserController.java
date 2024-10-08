package devping.nnplanner.domain.user.controller;

import devping.nnplanner.domain.user.dto.request.UserRequestDTO;
import devping.nnplanner.domain.user.service.UserService;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/check-pw")
    public ResponseEntity<ApiResponse<Void>> checkPassword(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UserRequestDTO userRequestDTO) {

        userService.checkPassword(userDetails, userRequestDTO);

        return GlobalResponse.OK("비밀번호 확인 성공", null);

    }

    @PatchMapping("edit-pw")
    public ResponseEntity<ApiResponse<Void>> editPassword(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UserRequestDTO userRequestDTO) {

        userService.editPassword(userDetails, userRequestDTO);

        return GlobalResponse.OK("비밀번호 수정 성공", null);
    }
}
