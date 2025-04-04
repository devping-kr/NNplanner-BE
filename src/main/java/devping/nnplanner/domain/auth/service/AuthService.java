package devping.nnplanner.domain.auth.service;

import devping.nnplanner.domain.auth.dto.request.AuthSignRequestDTO;
import devping.nnplanner.domain.auth.dto.request.GoogleInfoResponseDTO;
import devping.nnplanner.domain.auth.dto.request.GoogleLoginRequestDTO;
import devping.nnplanner.domain.auth.dto.request.GoogleRequestDTO;
import devping.nnplanner.domain.auth.dto.request.GoogleResponseDTO;
import devping.nnplanner.domain.auth.dto.response.AuthResponseDTO;
import devping.nnplanner.domain.auth.dto.response.AuthTokenResponseDTO;
import devping.nnplanner.domain.auth.repository.EmailRepository;
import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.domain.user.entity.User;
import devping.nnplanner.domain.user.entity.User.LoginType;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.token.JwtUtil;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientPw;
    @Value("${api.oauth.key}")
    private String oauthUrl;

    @Transactional
    public void signUp(AuthSignRequestDTO authSignRequestDTO) {

        if (userRepository.existsByEmail(authSignRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL);
        }
        ;
        if (!authSignRequestDTO.getPassword().equals(authSignRequestDTO.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }
        ;
        if (!emailRepository.existsByEmail(authSignRequestDTO.getEmail())
            && emailRepository.existsByEmailAndVerifiedIsFalse(authSignRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_EMAIL);
        }
        ;

        if (authSignRequestDTO.getLoginType().equals("LOCAL")) {

            User user = new User();

            user.create(
                authSignRequestDTO.getUsername(),
                authSignRequestDTO.getEmail(),
                passwordEncoder.encode(authSignRequestDTO.getPassword()),
                LoginType.LOCAL);

            user.setCreatedBy(authSignRequestDTO.getEmail());

            userRepository.save(user);
        }
    }

    public AuthTokenResponseDTO reissueToken(String refreshToken) {

        String reissueRefreshToken = jwtUtil.reissueRefreshToken(refreshToken);
        String reissueAccessToken = jwtUtil.reissueAccessToken(refreshToken);

        return new AuthTokenResponseDTO(reissueAccessToken, reissueRefreshToken);

    }

    public void logout(HttpServletRequest httpRequest, UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getUserId();
        String email = userDetails.getUser().getEmail();

        jwtUtil.deleteRefreshToken(userId, email);
        jwtUtil.logoutAccessToken(httpRequest);
    }

    public String loginUrlGoogle() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId
            + "&redirect_uri=" + oauthUrl
            + "&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }

    @Transactional
    public AuthResponseDTO loginGoogle(GoogleLoginRequestDTO googleLoginRequestDTO) {

        RestTemplate restTemplate = new RestTemplate();

        GoogleRequestDTO googleOAuthRequestParam = GoogleRequestDTO
            .builder()
            .clientId(googleClientId)
            .clientSecret(googleClientPw)
            .code(googleLoginRequestDTO.getAuthCode())
            .redirectUri(oauthUrl)
            .grantType("authorization_code")
            .build();

        ResponseEntity<GoogleResponseDTO> resultEntity = restTemplate.postForEntity(
            "https://oauth2.googleapis.com/token",
            googleOAuthRequestParam, GoogleResponseDTO.class);

        String jwtToken = resultEntity.getBody().getId_token();

        Map<String, String> map = new HashMap<>();
        map.put("id_token", jwtToken);

        ResponseEntity<GoogleInfoResponseDTO> resultEntity2 = restTemplate.postForEntity(
            "https://oauth2.googleapis.com/tokeninfo",
            map, GoogleInfoResponseDTO.class);

        String email = resultEntity2.getBody().getEmail();
        String name = resultEntity2.getBody().getName();

        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.create(
                name,
                email,
                null,
                LoginType.GOOGLE);
            userRepository.save(user);
        }

        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (user.getLoginType() == LoginType.LOCAL) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL);
        }

        String accessToken = jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId(), email);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO(user, accessToken, refreshToken);

        return authResponseDTO;
    }
}
