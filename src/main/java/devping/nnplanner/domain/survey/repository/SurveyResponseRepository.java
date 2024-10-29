package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    // 최신 SurveyResponse 조회
    SurveyResponse findTopBySurveyOrderByResponseDateDesc(Survey survey);

    // 특정 Survey에 연결된 모든 SurveyResponse 조회
    List<SurveyResponse> findBySurvey(Survey survey);
}
