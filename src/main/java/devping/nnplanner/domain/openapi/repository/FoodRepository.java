package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.Food;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoodRepository extends JpaRepository<Food, UUID> {

    boolean existsByFoodName(String foodName);

    @Query("SELECT f FROM Food f WHERE f.foodName LIKE %:hospitalFoodName% ORDER BY LENGTH(f.foodName) ASC")
    List<Food> findBySimilarName(@Param("hospitalFoodName") String hospitalFoodName);
}