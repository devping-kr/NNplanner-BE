package devping.nnplanner.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserRequestDTO {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).{8,}$",
        message = "비밀번호는 최소 8자 이상이어야 하며, 소문자와 숫자를 포함해야 합니다.")
    private String password;
}
