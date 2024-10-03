package devping.nnplanner.domain.survey.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Question {

    private String question;  // 질문 내용
    private String answerType;  // 답변 형식 (text, radio)

    public Question(String question, String answerType) {
        this.question = question;
        this.answerType = answerType;
    }
}