package devping.nnplanner.global.config;

import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return Optional.of(userDetails.getUser().getEmail());
        };
    }
}