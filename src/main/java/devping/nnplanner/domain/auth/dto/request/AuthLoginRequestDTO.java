package devping.nnplanner.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AuthLoginRequestDTO {

    @Size(max = 50)
    @NotBlank
    @Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String loginType;
}
