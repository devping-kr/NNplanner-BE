package devping.nnplanner.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailCodeRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verifyCode;
}
