package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SurveyDetailResponseDTO {

    private String surveyName;
    private List<MenuSelectionResponseDTO> likedMenusTop3;
    private List<MenuSelectionResponseDTO> dislikedMenusTop3;
    private List<String> desiredMenus;
    private List<String> messagesToDietitian;

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
        private String question;
        private Map<Integer, Integer> distribution;
        private String answerType;

        public QuestionSatisfactionDistribution(Long questionId, String question, Map<Integer, Integer> distribution, String answerType) {
            this.questionId = questionId;
            this.question = question;
            this.distribution = distribution;
            this.answerType = answerType;
        }
    }
}