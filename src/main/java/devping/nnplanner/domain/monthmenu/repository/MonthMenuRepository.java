package devping.nnplanner.domain.monthmenu.repository;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthMenuRepository extends JpaRepository<MonthMenu, UUID> {

    Page<MonthMenu> findAllByUser_UserId(Long userId, Pageable pageable);

    Integer countByUser_UserId(Long userId);

    Page<MonthMenu> findAllByUser_UserIdAndMenuCategory(Long userId,
                                                        MenuCategory menuCategory,
                                                        Pageable pageable);
}
