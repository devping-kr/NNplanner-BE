package devping.nnplanner.domain.openapi.batch.food;

import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.repository.FoodRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FoodItemWriter implements ItemWriter<List<Food>> {

    private final FoodRepository foodRepository;

    @Override
    public void write(Chunk<? extends List<Food>> items) {

        List<Food> foodsToSave = new ArrayList<>();

        items.getItems().forEach(foodList -> {
            foodList.forEach(food -> {
                if (!foodRepository.existsByFoodName(food.getFoodName())) {
                    foodsToSave.add(food);
                }
            });
        });

        foodRepository.saveAll(foodsToSave);
    }
}