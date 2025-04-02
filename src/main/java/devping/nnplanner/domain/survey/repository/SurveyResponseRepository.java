package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    // 최신 SurveyResponse 조회
    SurveyResponse findTopBySurveyOrderByResponseDateDesc(Survey survey);

    // 특정 Survey에 연결된 모든 SurveyResponse 조회
    @Query("SELECT sr FROM SurveyResponse sr join fetch  sr.responseDetails  WHERE sr.survey.id = :surveyId")
    Optional<List<SurveyResponse>> findBySurveyId(final Long surveyId);


}
