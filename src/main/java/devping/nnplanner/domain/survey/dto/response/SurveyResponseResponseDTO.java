package devping.nnplanner.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SurveyResponseResponseDTO {
    private final Long responseId;  // Long 타입으로 유지
    private final Long surveyId;
    private final LocalDateTime submittedAt;

    public SurveyResponseResponseDTO(Long responseId, Long surveyId, LocalDateTime submittedAt) {
        this.responseId = responseId;
        this.surveyId = surveyId;
        this.submittedAt = submittedAt;
    }
}