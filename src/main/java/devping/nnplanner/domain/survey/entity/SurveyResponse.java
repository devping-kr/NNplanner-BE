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

    private int satisfactionScore;
    private double totalSatisfaction;
    private double portionSatisfaction;
    private double hygieneSatisfaction;
    private double tasteSatisfaction;

    private LocalDateTime responseDate;
}
