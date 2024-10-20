package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import java.util.List;

public interface SchoolMenuRepositoryCustom {

    List<SchoolMenu> findRandomSchoolMenusBySchoolName(String schoolName, int dayCount);

    List<SchoolMenu> findRandomSchoolMenusBySchoolKindName(String schoolKindName, int dayCount);
}
