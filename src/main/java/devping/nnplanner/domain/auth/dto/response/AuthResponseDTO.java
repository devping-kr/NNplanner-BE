package devping.nnplanner.domain.auth.dto.response;

import devping.nnplanner.domain.auth.entity.User;
import lombok.Getter;

@Getter
public class AuthResponseDTO {

    private Long userId;

    private String username;

    private String email;

    private String authorization;

    public AuthResponseDTO(User user, String authorization) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.authorization = authorization;
    }
}
