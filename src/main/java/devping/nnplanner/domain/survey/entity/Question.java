package devping.nnplanner.domain.survey.entity;

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

    private String question;
    private String answerType;

    public Question(String question, String answerType) {
        this.question = question;
        this.answerType = answerType;
    }
}