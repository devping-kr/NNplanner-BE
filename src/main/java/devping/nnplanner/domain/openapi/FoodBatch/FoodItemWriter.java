package devping.nnplanner.domain.openapi.FoodBatch;

import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.repository.FoodRepository;
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

        items.getItems()
             .forEach(foodList -> foodRepository.saveAll(foodList));
    }
}