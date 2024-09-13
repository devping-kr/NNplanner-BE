package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.Food;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, UUID> {

}