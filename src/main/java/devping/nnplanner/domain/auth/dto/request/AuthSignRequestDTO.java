package devping.nnplanner.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AuthSignRequestDTO {

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 16)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).{8,}$",
        message = "비밀번호는 최소 8자 이상이어야 하며, 소문자와 숫자를 포함해야 합니다.")
    private String password;

    @NotBlank
    @Size(max = 16)
    private String passwordConfirm;

    @NotBlank
    private String loginType;
}
