package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SchoolInfoRepository extends JpaRepository<SchoolInfo, Long> {

    boolean existsBySchoolCode(String schoolCode);

    Optional<SchoolInfo> findBySchoolCode(String schoolCode);

    Optional<SchoolInfo> findTop1BySchoolKindName(String schoolKindName);

    Optional<SchoolInfo> findBySchoolName(String schoolName);

    @Query("SELECT DISTINCT s.schoolName FROM SchoolInfo s WHERE s.schoolName IS NOT NULL ORDER BY s.schoolName ASC")
    List<String> findDistinctSchoolNames();

    @Query("SELECT DISTINCT s.schoolKindName FROM SchoolInfo s WHERE s.schoolKindName IS NOT NULL")
    List<String> findDistinctSchoolKindNames();
}
