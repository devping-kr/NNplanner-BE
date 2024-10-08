package devping.nnplanner.domain.survey.dto.request;

import devping.nnplanner.domain.survey.entity.SurveyState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SurveyUpdateRequestDTO {

    private String surveyName;
    private LocalDateTime deadlineAt;
    private SurveyState state;
    private List<QuestionUpdateRequestDTO> questions;
}