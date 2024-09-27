package devping.nnplanner.domain.monthmenu.repository;

import devping.nnplanner.domain.monthmenu.entity.MonthMenuHospital;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthMenuHospitalRepository extends JpaRepository<MonthMenuHospital, UUID> {

    List<MonthMenuHospital> findAllByMonthMenu_MonthMenuId(UUID monthMenuId);

}
