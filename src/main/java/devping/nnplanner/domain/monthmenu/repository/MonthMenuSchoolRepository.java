package devping.nnplanner.domain.monthmenu.repository;

import devping.nnplanner.domain.monthmenu.entity.MonthMenuSchool;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthMenuSchoolRepository extends JpaRepository<MonthMenuSchool, UUID> {

    List<MonthMenuSchool> findAllByMonthMenu_MonthMenuId(UUID monthMenuId);

    Optional<MonthMenuSchool> findByMonthMenu_MonthMenuIdAndMenuDate(UUID monthMenuId,
                                                                     LocalDate menuDate);
}
