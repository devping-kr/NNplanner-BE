package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import java.util.List;

public interface HospitalMenuRepositoryCustom {

    List<HospitalMenu> findRandomHospitalMenusByCategory(String hospitalMenuKind, int dayCount);
}
