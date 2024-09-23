package devping.nnplanner.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EmailCodeRequestDTO {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    private String verifyCode;
}
