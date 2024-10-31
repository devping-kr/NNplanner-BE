package devping.nnplanner.global.jwt.token;

import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;

    @Value("${jwt.secret.key}")
    private String secret;

    public String createAccessToken(String email) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(email)
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                   .signWith(getSignKey(secret), SignatureAlgorithm.HS512)
                   .compact();
    }

    private SecretKey getSignKey(String secret) {

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createRefreshToken(Long userId, String email) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(email)
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 4))
                   .signWith(getSignKey(secret), SignatureAlgorithm.HS512)
                   .compact();
    }

    public void saveRefreshToken(Long userId, String refreshToken) {

        RefreshToken tokenEntity = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(tokenEntity);
    }

    public void deleteRefreshToken(Long userId, String refreshToken) {

        RefreshToken tokenEntity = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.delete(tokenEntity);
    }

    public String reissueRefreshToken(String refreshToken) {

        validateToken(refreshToken);

        Claims userInfo = getUserInfoFromToken(refreshToken);

        String email = userInfo.getSubject();
        Long userId = userInfo.get("userId", Long.class);

        RefreshToken storedRefreshToken =
            refreshTokenRepository.findById(userId)
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        String storedToken = storedRefreshToken.getRefreshToken();

        if (!storedToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        deleteRefreshToken(userId, refreshToken);
        String newRefreshToken = createRefreshToken(userId, email);
        saveRefreshToken(userId, newRefreshToken);

        return newRefreshToken;
    }

    public String reissueAccessToken(String refreshToken) {

        validateToken(refreshToken);

        Claims userInfo = getUserInfoFromToken(refreshToken);

        String email = userInfo.getSubject();

        return createAccessToken(email);
    }

    public void logoutAccessToken(HttpServletRequest httpRequest) {

        BlackList blackList = new BlackList(
            substringToken(
                httpRequest.getHeader("Authorization")));

        blackListRepository.save(blackList);
    }

    public String substringToken(String tokenValue) {

        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith("Bearer ")) {
            return tokenValue.substring(7);
        }

        throw new NullPointerException("토큰을 찾을 수 없습니다.");
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey(secret))
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    // 토큰 검증
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey(secret))
                .build()
                .parseClaimsJws(token);

            if (blackListRepository.existsById(token)) {
                throw new CustomException(ErrorCode.LOGOUT_JWT);
            }
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("유효하지 않는 JWT 서명 입니다.");
            throw new CustomException(ErrorCode.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT token 입니다.");
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 토큰 입니다.");
            throw new CustomException(ErrorCode.BAD_JWT);
        }

    }
}