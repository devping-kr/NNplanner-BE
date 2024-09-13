package devping.nnplanner.domain.openapi.FoodBatch;

import devping.nnplanner.domain.openapi.dto.response.FoodApiResponseDTO.FoodItem;
import devping.nnplanner.domain.openapi.entity.Food;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FoodItemProcessor implements ItemProcessor<List<FoodItem>, List<Food>> {

    @Override
    public List<Food> process(List<FoodItem> items) {

        return items.stream()
                    .map(item -> {
                        Food food = new Food();
                        food.setFoodName(item.getFoodName());
                        food.setCarbohydrate(item.getCarbohydrate());
                        food.setProtein(item.getProtein());
                        food.setFat(item.getFat());
                        food.setKcal(item.getKcal());
                        return food;
                    })
                    .collect(Collectors.toList());
    }
}