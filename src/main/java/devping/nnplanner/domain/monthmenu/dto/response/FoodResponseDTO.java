package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.openapi.entity.Food;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FoodResponseDTO {

    private final UUID foodId;

    private final String foodName;

    private final String carbohydrate;

    private final String protein;

    private final String fat;

    private final String kcal;

    public FoodResponseDTO(Food food) {
        this.foodId = food.getFoodId();
        this.foodName = food.getFoodName();
        this.carbohydrate = food.getCarbohydrate();
        this.protein = food.getProtein();
        this.fat = food.getFat();
        this.kcal = food.getKcal();
    }
}