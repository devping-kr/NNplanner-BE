package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Question;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

//    @Query("SELECT new devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO(sr.responseDate, sr.likedMenus) " +
//        "FROM SurveyResponse sr WHERE sr.survey.id = :surveyId " +
//        "GROUP BY sr.likedMenus, sr.responseDate ORDER BY COUNT(sr) DESC")
//    List<MenuSelectionResponseDTO> findTopLikedMenus(@Param("surveyId") Long surveyId);
//
//    // 상위 싫어하는 메뉴 3개 조회
//    @Query("SELECT new devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO(sr.responseDate, sr.dislikedMenus) " +
//        "FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.dislikedMenus, sr.responseDate ORDER BY COUNT(sr) DESC")
//    List<MenuSelectionResponseDTO> findTopDislikedMenus(Long surveyId);

    // 사용자가 먹고 싶은 메뉴 조회
    @Query("SELECT sr.desiredMenus FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    List<String> findDesiredMenus(Long surveyId);

    // 영양사에게 남긴 메시지 조회
    @Query("SELECT sr.messagesToDietitian FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    List<String> findMessagesToDietitian(Long surveyId);

    // 만족도 분포 조회 메서드들 추가 (이미 존재하는 경우 생략 가능)
    @Query("SELECT sr.monthlySatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.monthlySatisfaction")
    List<Object[]> getMonthlySatisfactionDistribution(@Param("surveyId") Long surveyId);

    @Query("SELECT sr.portionSatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.portionSatisfaction")
    List<Object[]> getPortionSatisfactionDistribution(Long surveyId);

    @Query("SELECT sr.hygieneSatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.hygieneSatisfaction")
    List<Object[]> getHygieneSatisfactionDistribution(Long surveyId);

    @Query("SELECT sr.tasteSatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.tasteSatisfaction")
    List<Object[]> getTasteSatisfactionDistribution(Long surveyId);

    @Query("SELECT AVG(sr.monthlySatisfaction), AVG(sr.portionSatisfaction), AVG(sr.hygieneSatisfaction), AVG(sr.tasteSatisfaction) " +
        "FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    List<Object[]> findAverageScores(@Param("surveyId") Long surveyId);


    SurveyResponse findTopBySurveyOrderByResponseDateDesc(Survey survey);

    List<SurveyResponse> findBySurvey(Survey survey);

    List<SurveyResponse> findBySurveyAndQuestion(Survey survey, Question question);
}
