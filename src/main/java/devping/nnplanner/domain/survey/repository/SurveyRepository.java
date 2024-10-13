package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.entity.SurveyState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query("SELECT s FROM Survey s WHERE "
        + "LOWER(s.surveyName) LIKE LOWER(CONCAT('%', :search, '%')) AND "
        + "s.createdAt >= :startDate AND "
        + "s.createdAt <= :endDate AND "
        + "(:state IS NULL OR s.state = :state)")
    Page<Survey> findSurveys(@Param("search") String search,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("state") SurveyState state,
                             Pageable pageable);
}
