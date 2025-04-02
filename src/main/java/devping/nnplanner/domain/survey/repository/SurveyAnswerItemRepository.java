package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.SurveyAnswerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SurveyAnswerItemRepository extends JpaRepository<SurveyAnswerItem, Long> {

    @Query("SELECT sai FROM SurveyAnswerItem sai WHERE sai.surveyResponseDetail.id = :responseDetailId")
    List<SurveyAnswerItem> findSurveyAnswerItemByResponseDetailId(Long responseDetailId);


}
