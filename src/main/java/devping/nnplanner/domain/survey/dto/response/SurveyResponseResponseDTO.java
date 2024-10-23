package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;

@Getter
public class SurveyResponseResponseDTO {
    private final Long responseId;
    private final Long surveyId;

    public SurveyResponseResponseDTO(Long responseId, Long surveyId) {
        this.responseId = responseId;
        this.surveyId = surveyId;
    }
}