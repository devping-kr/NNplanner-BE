package devping.nnplanner.domain.survey.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyResponseRequestDTO {

    private List<QuestionResponseDTO> basicQuestions;
    private List<QuestionResponseDTO> additionalQuestions;

    @Getter
    @Setter
    public static class QuestionResponseDTO {
        private Long questionId;
        private Object answer; // 답변은 문자열 또는 리스트
    }
}
