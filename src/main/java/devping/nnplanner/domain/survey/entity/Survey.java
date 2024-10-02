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

    private LocalDateTime deadlineAt;

    @Enumerated(EnumType.STRING)
    private SurveyState state;

    @ElementCollection
    @CollectionTable(name = "survey_questions", joinColumns = @JoinColumn(name = "survey_id"))
    private List<Question> questions = new ArrayList<>();

    public Survey(MonthMenu monthMenu, LocalDateTime deadlineAt, List<Question> questions) {
        this.monthMenu = monthMenu;
        this.deadlineAt = deadlineAt != null ? deadlineAt : LocalDateTime.now().plusWeeks(2);
        this.questions = questions;
        this.state = SurveyState.IN_PROGRESS;
    }

    public void checkDeadline() {
        if (this.deadlineAt.isBefore(LocalDateTime.now())) {
            this.state = SurveyState.CLOSED;
        }
    }
}
