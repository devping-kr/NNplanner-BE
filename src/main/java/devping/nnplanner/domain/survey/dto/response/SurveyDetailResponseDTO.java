package devping.nnplanner.domain.survey.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
public class SurveyDetailResponseDTO {

    private String surveyName;
    private LocalDateTime deadline;
    private UUID mmId;

    private List<QuestionSatisfactionDistribution> mandatoryQuestions;  // 기본 질문 리스트
    private List<QuestionSatisfactionDistribution> additionalQuestions; // 추가 질문 리스트

    private AverageScores averageScores;

    @Builder
    private SurveyDetailResponseDTO(String surveyName, LocalDateTime deadline, UUID mmId, List<QuestionSatisfactionDistribution> mandatoryQuestions, List<QuestionSatisfactionDistribution> additionalQuestions, AverageScores averageScores) {
        this.surveyName = surveyName;
        this.deadline = deadline;
        this.mmId = mmId;
        this.mandatoryQuestions = mandatoryQuestions;
        this.additionalQuestions = additionalQuestions;
        this.averageScores = averageScores;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AverageScores {
        private double totalSatisfaction;
        private double portionSatisfaction;
        private double hygieneSatisfaction;
        private double tasteSatisfaction;

        @Builder
        public AverageScores(double totalSatisfaction, double portionSatisfaction, double hygieneSatisfaction, double tasteSatisfaction) {
            this.totalSatisfaction = totalSatisfaction;
            this.portionSatisfaction = portionSatisfaction;
            this.hygieneSatisfaction = hygieneSatisfaction;
            this.tasteSatisfaction = tasteSatisfaction;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)

    public static class QuestionSatisfactionDistribution {
        private Long questionId;
        private String questionText;
        private Map<Integer, Integer> radioResponses; // "radio" 타입 질문에 대해서만 설정됨
        private List<String> textResponses; // "text" 타입 질문에 대해서만 설정됨
        private String answerType;

        @Builder
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

        @Builder
        private QuestionSatisfactionDistribution(Long questionId, String questionText, String answerType) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.radioResponses = Map.of();
            this.textResponses = List.of();
            this.answerType = answerType;
        }
    }
}
