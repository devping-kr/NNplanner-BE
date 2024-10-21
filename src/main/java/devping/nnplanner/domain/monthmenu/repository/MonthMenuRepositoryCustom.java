package devping.nnplanner.domain.monthmenu.repository;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MonthMenuRepositoryCustom {

    Page<MonthMenu> searchByMenuNameOrDateAndCategory(Long userId,
                                                      String majorCategory,
                                                      String minorCategory,
                                                      String menuName,
                                                      Integer year,
                                                      Integer month,
                                                      Pageable pageable);
}