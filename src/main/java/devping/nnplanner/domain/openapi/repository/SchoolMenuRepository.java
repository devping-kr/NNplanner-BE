package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolMenuRepository
    extends JpaRepository<SchoolMenu, UUID>, SchoolMenuRepositoryCustom {

    boolean existsBySchoolInfo_SchoolInfoIdAndFood1AndFood2AndFood3AndFood4AndFood5AndFood6AndFood7(
        Long schoolInfoId,
        Food food1,
        Food food2,
        Food food3,
        Food food4,
        Food food5,
        Food food6,
        Food food7);
}
