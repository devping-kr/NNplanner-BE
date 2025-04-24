package devping.nnplanner.domain.survey.dto.response;

import devping.nnplanner.domain.survey.entity.SurveyState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SurveyUpdateResponseDTO {

    private UUID surveyId;
    private String surveyName;
    private LocalDateTime deadlineAt;
    private SurveyState surveyState;
    private List<QuestionResponseDTO> questions;

    @Getter
    @Setter
    public static class QuestionResponseDTO {
        private Long questionId;
        private LocalDateTime updatedAt;

        public QuestionResponseDTO(Long questionId, LocalDateTime updatedAt) {
            this.questionId = questionId;
            this.updatedAt = updatedAt;
        }
    }

    // 생성자 수정
    public SurveyUpdateResponseDTO(UUID surveyId, String surveyName, LocalDateTime deadlineAt, SurveyState surveyState, List<QuestionResponseDTO> updatedQuestions) {
        this.surveyId = surveyId;
        this.surveyName = surveyName;
        this.deadlineAt = deadlineAt;
        this.surveyState = surveyState;
        this.questions = updatedQuestions;
    }
}