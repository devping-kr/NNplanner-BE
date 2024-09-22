package devping.nnplanner.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient foodWebClient() {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(
            "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo01/getFoodNtrCpntDbInq01");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        ExchangeStrategies strategies =
            ExchangeStrategies.builder()
                              .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(
                                  16 * 1024 * 1024)) // 16MB
                              .build();

        return WebClient.builder()
                        .uriBuilderFactory(factory)
                        .exchangeStrategies(strategies)
                        .build();
    }

    @Bean
    public WebClient schoolWebClient() {

        ExchangeStrategies strategies =
            ExchangeStrategies.builder()
                              .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(
                                  16 * 1024 * 1024)) // 16MB
                              .build();

        return WebClient.builder()
                        .baseUrl(
                            "https://open.neis.go.kr/hub")
                        .exchangeStrategies(strategies)
                        .build();
    }
}
