package devping.nnplanner.domain.survey.entity;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    private String surveyName;

    @Setter
    @Column(name = "deadline_at")
    private LocalDateTime deadlineAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private SurveyState state;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "survey_id")  // Survey의 id를 참조하도록 설정
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SurveyResponse> responses = new ArrayList<>();

    public Survey(MonthMenu monthMenu, String surveyName, LocalDateTime deadlineAt, List<Question> questions) {
        this.monthMenu = monthMenu;
        this.surveyName = surveyName;
        this.deadlineAt = deadlineAt;
        this.questions = questions;
        this.state = SurveyState.IN_PROGRESS;
    }
}