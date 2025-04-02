package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.SurveyResponse;
import devping.nnplanner.domain.survey.entity.SurveyResponseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResponseDetailRepository extends JpaRepository<SurveyResponseDetail, Long> {

    // 특정 SurveyResponse에 연결된 ResponseDetail 조회")
    Optional<List<SurveyResponseDetail>> findSurveyResponseDetailBySurveyResponse(SurveyResponse surveyResponse);
//
//    // 특정 질문 ID에 따라 분류된 응답 조회 예시
//    @Query("SELECT rd FROM SurveyResponseDetail rd WHERE rd.question.id = :questionId AND rd.surveyResponse.survey.id = :surveyId")
//    List<SurveyResponseDetail> findByQuestionAndSurvey(@Param("questionId") Long questionId, @Param("surveyId") Long surveyId);
//
//    // 영양사에게 남긴 메시지 조회 (answerText가 NULL이 아닌 경우)
//    @Query("SELECT rd.answerText FROM SurveyResponseDetail rd WHERE rd.surveyResponse.survey.id = :surveyId AND rd.answerText IS NOT NULL")
//    List<String> findMessagesToDietitian(@Param("surveyId") Long surveyId);
//
//    // 만족도나 기타 항목의 평균값 계산 예시
//    @Query("SELECT AVG(rd.answerScore) FROM SurveyResponseDetail rd WHERE rd.surveyResponse.survey.id = :surveyId")
//    Double findAverageSatisfactionScoreBySurvey(@Param("surveyId") Long surveyId);
}
