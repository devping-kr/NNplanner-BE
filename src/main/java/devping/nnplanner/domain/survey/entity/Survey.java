package devping.nnplanner.domain.survey.entity;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "surveys")
@Getter
@NoArgsConstructor
@Entity
public class Survey extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "month_menu_id", nullable = false)
    private MonthMenu monthMenu;

    private String surveyName;

    @Column(name = "deadline_at")
    private LocalDateTime deadlineAt;

    @Enumerated(EnumType.STRING)
    private SurveyState state;

    @ElementCollection
    @CollectionTable(name = "survey_questions", joinColumns = @JoinColumn(name = "survey_id"))
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SurveyResponse> responses = new ArrayList<>();

    public Survey(MonthMenu monthMenu, String surveyName, LocalDateTime deadlineAt, List<Question> questions) {
        this.monthMenu = monthMenu;
        this.surveyName = surveyName;
        this.deadlineAt = deadlineAt;
        this.questions = questions;
    }
}
