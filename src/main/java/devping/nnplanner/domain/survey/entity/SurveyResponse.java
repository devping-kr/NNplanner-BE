package devping.nnplanner.domain.survey.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class SurveyResponse extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private String likedMenus;
    private String dislikedMenus;

    @ElementCollection
    private List<String> desiredMenus;

    @Column(name = "messages_to_dietitian")
    private String messagesToDietitian;

    private int monthlySatisfaction;
    private int portionSatisfaction;
    private int hygieneSatisfaction;
    private int tasteSatisfaction;

    @Column(name = "total_satisfaction", nullable = false)
    private int totalSatisfaction;

    @Column(name = "satisfaction_score", nullable = false)
    private int satisfactionScore;

    private LocalDateTime responseDate;

    public SurveyResponse(Survey survey, String likedMenus, String dislikedMenus,
                          List<String> desiredMenus, String messagesToDietitian,
                          int monthlySatisfaction, int portionSatisfaction,
                          int hygieneSatisfaction, int tasteSatisfaction,
                          LocalDateTime responseDate) {
        this.survey = survey;
        this.likedMenus = likedMenus;
        this.dislikedMenus = dislikedMenus;
        this.desiredMenus = desiredMenus;
        this.messagesToDietitian = messagesToDietitian;
        this.monthlySatisfaction = monthlySatisfaction;
        this.portionSatisfaction = portionSatisfaction;
        this.hygieneSatisfaction = hygieneSatisfaction;
        this.tasteSatisfaction = tasteSatisfaction;
        this.totalSatisfaction = calculateTotalSatisfaction();
        this.satisfactionScore = calculateSatisfactionScore();
        this.responseDate = responseDate;
    }

    // 총 만족도를 계산하는 메서드
    public int calculateTotalSatisfaction() {
        return (monthlySatisfaction + portionSatisfaction + hygieneSatisfaction + tasteSatisfaction) / 4;
    }

    // satisfactionScore 계산 메서드
    public int calculateSatisfactionScore() {
        // 여기서 원하는 로직으로 satisfactionScore를 계산
        return (monthlySatisfaction + portionSatisfaction + hygieneSatisfaction + tasteSatisfaction) / 4;
    }
}
