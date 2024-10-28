package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SurveyDetailResponseDTO {

    private String surveyName;
    private LocalDateTime deadline;

    private List<QuestionSatisfactionDistribution> mandatoryQuestions;  // 기본 질문 리스트
    private List<QuestionSatisfactionDistribution> additionalQuestions; // 추가 질문 리스트

    private AverageScores averageScores;

    @Getter
    @Setter
    public static class AverageScores {
        private double totalSatisfaction;
        private double portionSatisfaction;
        private double hygieneSatisfaction;
        private double tasteSatisfaction;
    }

    @Getter
    @Setter
    public static class QuestionSatisfactionDistribution {
        private Long questionId;
        private String questionText;
        private Map<Integer, Integer> radioResponses; // "radio" 타입 질문에 대해서만 설정됨
        private List<String> textResponses; // "text" 타입 질문에 대해서만 설정됨
        private String answerType;

        public QuestionSatisfactionDistribution(Long questionId, String questionText,
                                                Map<Integer, Integer> radioResponses,
                                                List<String> textResponses,
                                                String answerType) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.radioResponses = radioResponses;
            this.textResponses = textResponses;
            this.answerType = answerType;
        }

        public QuestionSatisfactionDistribution(Long questionId, String questionText, String answerType) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.radioResponses = Map.of();
            this.textResponses = List.of();
            this.answerType = answerType;
        }
    }
}
