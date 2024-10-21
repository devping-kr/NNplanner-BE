package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.Recipe;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    List<Recipe> findByMonth(Integer month);

}
