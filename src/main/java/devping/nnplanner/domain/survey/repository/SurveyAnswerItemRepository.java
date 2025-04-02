package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.SurveyAnswerItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyAnswerItemRepository extends JpaRepository<SurveyAnswerItem, Long> {
}
