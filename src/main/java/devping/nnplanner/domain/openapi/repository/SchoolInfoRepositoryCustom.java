package devping.nnplanner.domain.openapi.repository;

import java.util.List;

public interface SchoolInfoRepositoryCustom {

    List<String> findDistinctSchoolNames();

    List<String> findDistinctSchoolKindNames();

    List<String> searchSchoolNames(String keyword, int limit);
}
