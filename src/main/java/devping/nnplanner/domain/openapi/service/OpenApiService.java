package devping.nnplanner.domain.openapi.service;

import devping.nnplanner.domain.openapi.dto.response.RecipeResponseDTO;
import devping.nnplanner.domain.openapi.repository.RecipeRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final RecipeRepository recipeRepository;

    public List<RecipeResponseDTO> getRecipe() {

        return recipeRepository.findByMonth(LocalDate.now().getMonthValue())
                               .stream()
                               .map(RecipeResponseDTO::new)
                               .toList();
    }

}
