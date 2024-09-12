package devping.nnplanner.global.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://default-nnplanner-cd7c0-26459303-247c8a4f5ac3.kr.lb.naverncp.com"
        ));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}