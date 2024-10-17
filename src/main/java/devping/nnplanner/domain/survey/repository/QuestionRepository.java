package devping.nnplanner.domain.survey.repository;

import devping.nnplanner.domain.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
