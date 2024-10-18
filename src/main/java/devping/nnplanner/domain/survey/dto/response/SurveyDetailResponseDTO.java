package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SurveyDetailResponseDTO {

    private String surveyName;

    private List<QuestionSatisfactionDistribution> satisfactionDistributions;

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
        private Map<Integer, Integer> satisfactionDistribution; // "radio" 타입 질문에 대해서만 설정됨
        private List<String> textResponses; // "text" 타입 질문에 대해서만 설정됨
        private String answerType;

        // 수정된 생성자
        public QuestionSatisfactionDistribution(Long questionId, String questionText,
                                                Map<Integer, Integer> satisfactionDistribution,
                                                List<String> textResponses,
                                                String answerType) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.satisfactionDistribution = satisfactionDistribution;
            this.textResponses = textResponses;
            this.answerType = answerType;
        }

        // 추가적인 생성자: 필요 시 기본값으로 초기화하는 생성자 추가
        public QuestionSatisfactionDistribution(Long questionId, String questionText, String answerType) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.satisfactionDistribution = Map.of();
            this.textResponses = List.of();
            this.answerType = answerType;
        }
    }
}
