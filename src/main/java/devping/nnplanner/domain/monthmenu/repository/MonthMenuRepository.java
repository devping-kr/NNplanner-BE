package devping.nnplanner.domain.monthmenu.repository;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthMenuRepository extends JpaRepository<MonthMenu, UUID> {

    Page<MonthMenu> findAllByUser_UserId(Long userId, Pageable pageable);
}
