package devping.nnplanner.domain.survey.entity;

import devping.nnplanner.domain.auth.entity.User;
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

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SurveyResponse> responses = new ArrayList<>();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Survey(MonthMenu monthMenu, String surveyName, LocalDateTime deadlineAt, List<Question> questions) {
        this.monthMenu = monthMenu;
        this.surveyName = surveyName;
        this.deadlineAt = deadlineAt;
        this.questions = questions;
        this.state = SurveyState.IN_PROGRESS;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setSurvey(this);
    }
}