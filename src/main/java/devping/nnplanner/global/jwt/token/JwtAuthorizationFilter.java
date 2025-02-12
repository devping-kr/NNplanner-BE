package devping.nnplanner.global.jwt.token;

import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = request.getHeader("Authorization");

        if (StringUtils.hasText(tokenValue)) {

            tokenValue = jwtUtil.substringToken(tokenValue);

            try {
                jwtUtil.validateToken(tokenValue);

                Claims userinfo = jwtUtil.getUserInfoFromToken(tokenValue);
                setAuthentication(userinfo.getSubject());

            } catch (CustomException e) {
                setErrorResponse(response, e.getErrorCode());
                return;
            } catch (Exception e) {
                log.error(e.getMessage());
                setErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String email) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode)
        throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        // CORS 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        response.getWriter().write(
            "{\"status\": " + errorCode.getStatus().value() + ", " +
                "\"message\": \"" + errorCode.getMessage() + "\"}"
        );
    }
}