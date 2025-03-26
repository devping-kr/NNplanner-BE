package devping.nnplanner.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${api.swagger.key}")
    private String swaggerUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .servers(List.of(new Server().url(swaggerUrl)))
            .info(new Info()
                .title("냠냠플래너")
                .description("냠냠플래너 스웨거 입니다.")
                .version("v1.0.0")
                .contact(new Contact()
                    .email("plannernn@gmail.com")
                    .url("https://www.nnplanner.com")));
    }
}