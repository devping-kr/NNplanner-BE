package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndSurveyId(Long questionId, Long surveyId);
}
