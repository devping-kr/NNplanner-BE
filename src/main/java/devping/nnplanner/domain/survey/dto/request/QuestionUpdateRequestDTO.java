package devping.nnplanner.domain.survey.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionUpdateRequestDTO {

    private Long questionId;
    private String question;
}