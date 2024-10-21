package devping.nnplanner.domain.openapi.dto.response;

import devping.nnplanner.domain.openapi.entity.Recipe;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RecipeResponseDTO {

    private final UUID recipeId;
    private final Integer month;
    private final String recipeName;
    private final String mainIngredient;
    private final String subIngredient;
    private final String instructions;
    private final String forGroup;
    private final String imageUrl;

    public RecipeResponseDTO(Recipe recipe) {

        this.recipeId = recipe.getRecipeId();
        this.month = recipe.getMonth();
        this.recipeName = recipe.getRecipeName();
        this.mainIngredient = recipe.getMainIngredient();
        this.subIngredient = recipe.getSubIngredient();
        this.instructions = recipe.getInstructions();
        this.forGroup = recipe.getForGroup();
        this.imageUrl = recipe.getImageUrl();
    }
}
