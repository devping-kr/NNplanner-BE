package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query("SELECT s FROM Survey s WHERE "
        + "LOWER(s.surveyName) LIKE LOWER(CONCAT('%', :search, '%')) AND "
        + "s.createdAt >= :startDate AND "
        + "s.createdAt <= :endDate")
    Page<Survey> findSurveys(@Param("search") String search,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             Pageable pageable);
}
