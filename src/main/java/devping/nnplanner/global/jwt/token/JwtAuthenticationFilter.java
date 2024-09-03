package devping.nnplanner.global.jwt.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import devping.nnplanner.domain.auth.dto.request.AuthRequestDTO;
import devping.nnplanner.domain.auth.dto.response.AuthResponseDTO;
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
            AuthRequestDTO requestDTO =
                new ObjectMapper().readValue(request.getInputStream(), AuthRequestDTO.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDTO.getEmail(),
                    requestDTO.getPassword(),
                    null
                )
            );
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

        log.info("로그인 성공 및 JWT 생성");

        UserDetailsImpl userDetails = ((UserDetailsImpl) authResult.getPrincipal());

        String token = jwtUtil.createToken(userDetails.getUser().getEmail());

        AuthResponseDTO responseDTO = new AuthResponseDTO(userDetails.getUser(), token);

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