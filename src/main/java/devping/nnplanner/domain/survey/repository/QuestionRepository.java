package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndSurveyId(Long questionId, UUID surveyId);

    Optional<List<Question>> findAllBySurveyId(UUID surveyId);
}
