package devping.nnplanner.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthLoginRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
