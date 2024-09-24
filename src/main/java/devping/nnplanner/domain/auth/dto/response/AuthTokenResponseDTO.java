package devping.nnplanner.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class AuthTokenResponseDTO {

    private final String accessToken;

    private final String refreshToken;

    public AuthTokenResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}