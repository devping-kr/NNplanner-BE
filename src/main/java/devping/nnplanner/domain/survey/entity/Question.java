package devping.nnplanner.domain.survey.entity;

import com.fasterxml.jackson.databind.introspect.Annotated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answerType;

    @Column(name = "is_mandatory")
    private boolean isMandatory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    public Question(String question, String answerType, boolean isMandatory, Survey survey) {
        this.question = question;
        this.answerType = answerType;
        this.isMandatory = isMandatory;
        this.survey = survey;
    }

    public void reviseQuestion(final String newContent, final String answerType) {
        this.question = newContent;
        this.answerType = answerType;
    }
}