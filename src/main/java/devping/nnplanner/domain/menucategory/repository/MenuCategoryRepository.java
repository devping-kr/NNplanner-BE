package devping.nnplanner.domain.menucategory.repository;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    Optional<MenuCategory> findByMajorCategoryAndMinorCategory(String majorCategory,
                                                               String minorCategory);
}
