package devping.nnplanner.domain.survey.entity;

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

    private Long mmId;

    private LocalDateTime deadlineAt;

    @Enumerated(EnumType.STRING)
    private SurveyState state;

    @ElementCollection
    @CollectionTable(name = "questions", joinColumns = @JoinColumn(name = "survey_id"))
    @Column(name = "question")
    private List<String> questions = new ArrayList<>();

    public Survey(Long mmId, LocalDateTime deadlineAt, List<String> questions) {
        this.mmId = mmId;
        this.deadlineAt = deadlineAt != null ? deadlineAt : LocalDateTime.now().plusWeeks(2); // 기본 마감 기한 설정
        this.questions = questions;
        this.state = SurveyState.IN_PROGRESS;
    }

    public void checkDeadline() {
        if (this.deadlineAt.isBefore(LocalDateTime.now())) {
            this.state = SurveyState.CLOSED;
        }
    }
}
