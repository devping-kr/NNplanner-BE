package devping.nnplanner.domain.auth.dto.response;

import devping.nnplanner.domain.auth.entity.User;
import lombok.Getter;

@Getter
public class AuthResponseDTO {

    private Long userId;

    private String username;

    private String email;

    private String accessToken;

    private String refreshToken;

    public AuthResponseDTO(User user, String accessToken, String refreshToken) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
