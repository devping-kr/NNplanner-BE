package devping.nnplanner.domain.auth.dto.response;

import devping.nnplanner.domain.user.entity.User;
import lombok.Getter;

@Getter
public class AuthResponseDTO {

    private final Long userId;

    private final String username;

    private final String email;

    private final String accessToken;

    private final String refreshToken;

    public AuthResponseDTO(User user, String accessToken, String refreshToken) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
