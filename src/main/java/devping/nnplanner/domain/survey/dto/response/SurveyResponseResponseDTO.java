package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SurveyResponseResponseDTO {
    private final Long responseId;
    private final UUID surveyId;

    public SurveyResponseResponseDTO(Long responseId, UUID surveyId) {
        this.responseId = responseId;
        this.surveyId = surveyId;
    }
}