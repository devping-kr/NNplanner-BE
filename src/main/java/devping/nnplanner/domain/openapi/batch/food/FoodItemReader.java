package devping.nnplanner.domain.openapi.batch.food;

import devping.nnplanner.domain.openapi.dto.response.FoodApiResponseDTO;
import devping.nnplanner.domain.openapi.dto.response.FoodApiResponseDTO.FoodItem;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class FoodItemReader implements ItemReader<List<FoodItem>> {

    private final WebClient webClient;
    private Integer pageNo = 1;
    private final Integer numOfRows = 100;

    @Value("${api.food.key}")
    private String apiKey;

    @Autowired
    public FoodItemReader(WebClient foodWebClient) {
        this.webClient = foodWebClient;
    }

    @Override
    public List<FoodItem> read() {

        if (pageNo <= 4) {
            synchronized (this) {
                return processPage();
            }
        }

        return processPage();
    }

    private List<FoodItem> processPage() {

        FoodApiResponseDTO response =
            webClient.get()
                     .uri(uriBuilder -> uriBuilder
                         .queryParam("serviceKey", apiKey)
                         .queryParam("pageNo", pageNo)
                         .queryParam("numOfRows", numOfRows)
                         .queryParam("type", "json")
                         .build())
                     .retrieve()
                     .bodyToMono(FoodApiResponseDTO.class)
                     .block();

        if (response == null) {
            return null;
        }

        log.info("현재 페이지 : {}", pageNo);

        pageNo++;

        return response.getBody().getItems();
    }
}
