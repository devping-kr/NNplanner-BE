package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO;
import devping.nnplanner.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    // 상위 좋아하는 메뉴 3개 조회
    @Query("SELECT new devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO(sr.responseDate, sr.likedMenus) " +
        "FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.likedMenus, sr.responseDate ORDER BY COUNT(sr) DESC")
    List<MenuSelectionResponseDTO> findTopLikedMenus(Long surveyId);

    // 상위 싫어하는 메뉴 3개 조회
    @Query("SELECT new devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO(sr.responseDate, sr.dislikedMenus) " +
        "FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.dislikedMenus, sr.responseDate ORDER BY COUNT(sr) DESC")
    List<MenuSelectionResponseDTO> findTopDislikedMenus(Long surveyId);

    // 사용자가 먹고 싶은 메뉴 조회
    @Query("SELECT sr.desiredMenus FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    List<String> findDesiredMenus(Long surveyId);

    // 영양사에게 남긴 메시지 조회
    @Query("SELECT sr.messagesToDietitian FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    List<String> findMessagesToDietitian(Long surveyId);

    // monthlySatisfaction 분포 조회
    @Query("SELECT sr.monthlySatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.monthlySatisfaction")
    List<Object[]> getMonthlySatisfactionDistribution(Long surveyId);

    // portionSatisfaction 분포 조회
    @Query("SELECT sr.portionSatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.portionSatisfaction")
    List<Object[]> getPortionSatisfactionDistribution(Long surveyId);

    // hygieneSatisfaction 분포 조회
    @Query("SELECT sr.hygieneSatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.hygieneSatisfaction")
    List<Object[]> getHygieneSatisfactionDistribution(Long surveyId);

    // tasteSatisfaction 분포 조회
    @Query("SELECT sr.tasteSatisfaction, COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId GROUP BY sr.tasteSatisfaction")
    List<Object[]> getTasteSatisfactionDistribution(Long surveyId);

    // 평균 점수 조회
    @Query("SELECT AVG(sr.monthlySatisfaction), AVG(sr.portionSatisfaction), AVG(sr.hygieneSatisfaction), AVG(sr.tasteSatisfaction) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Object[] findAverageScores(Long surveyId);
}
