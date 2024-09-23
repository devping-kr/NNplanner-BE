package devping.nnplanner.global.jwt.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import devping.nnplanner.domain.auth.dto.request.AuthLoginRequestDTO;
import devping.nnplanner.domain.auth.dto.response.AuthResponseDTO;
import devping.nnplanner.domain.auth.entity.User.LoginType;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auths/login");
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        log.info("로그인 시도");
        try {
            AuthLoginRequestDTO requestDTO =
                new ObjectMapper().readValue(request.getInputStream(), AuthLoginRequestDTO.class);

            if (requestDTO.getLoginType().equals("LOCAL")) {

                return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword(),
                        null
                    )
                );
            } else {
                log.warn("잘못된 로그인 타입: {}", requestDTO.getLoginType());
                throw new AuthenticationException(
                    "지원하지 않는 로그인 타입입니다: " + requestDTO.getLoginType()) {
                };
            }
        } catch (IOException e) {
            log.error("요청 데이터 읽기 오류: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult) throws IOException {

        UserDetailsImpl userDetails = ((UserDetailsImpl) authResult.getPrincipal());

        if (!(userDetails.getUser().getLoginType() == LoginType.LOCAL)) {
            throw new AuthenticationException("로그인 타입을 확인해주세요.") {
            };
        }

        log.info("로그인 성공 및 JWT 생성");

        String email = userDetails.getUser().getEmail();
        Long userId = userDetails.getUser().getUserId();

        String accessToken = jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken(userId, email);

        jwtUtil.saveRefreshToken(userId, refreshToken);

        AuthResponseDTO responseDTO = new AuthResponseDTO(userDetails.getUser(), accessToken,
            refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(
                    ApiResponse.of("로그인 성공", responseDTO)));
    }

    @Override
    protected void unsuccessfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed) throws IOException {

        log.info("로그인 실패:{}", failed.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper()
            .writeValueAsString(
                ApiResponse.of("로그인 실패 : " + failed.getMessage(), null)));
    }
}