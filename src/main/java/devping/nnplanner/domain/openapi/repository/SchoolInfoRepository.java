package devping.nnplanner.domain.openapi.repository;

import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolInfoRepository
    extends JpaRepository<SchoolInfo, Long>, SchoolInfoRepositoryCustom {

    boolean existsBySchoolCode(String schoolCode);

    Optional<SchoolInfo> findBySchoolCode(String schoolCode);

    Optional<SchoolInfo> findTop1BySchoolKindName(String schoolKindName);

    Optional<SchoolInfo> findBySchoolName(String schoolName);
}
