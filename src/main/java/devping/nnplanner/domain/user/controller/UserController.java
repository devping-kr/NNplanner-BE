package devping.nnplanner.domain.user.controller;

import devping.nnplanner.domain.user.dto.request.UserRequestDTO;
import devping.nnplanner.domain.user.service.UserService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signUp(
        @RequestBody UserRequestDTO userRequestDTO) {

        return ResponseUtil.OK(ApiResponse.of("유저 회원가입 성공", null));

    }

}
