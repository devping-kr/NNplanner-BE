package devping.nnplanner.domain.survey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SurveyResponseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_response_id", nullable = false)
    private SurveyResponse surveyResponse;  // 상위 SurveyResponse와 연관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;  // 질문과 연관

    @Column(name = "answer_score")
    private Integer answerScore;  // radio 타입 점수 저장

    @Column(name = "answer_text", length = 1000)
    private String answerText;  // text 타입의 단일 텍스트 응답 저장

    @ElementCollection
    @CollectionTable(name = "survey_response_answer_list", joinColumns = @JoinColumn(name = "survey_response_detail_id"))
    @Column(name = "answer_item")
    private List<String> answerList;  // text 타입의 리스트 응답 저장 (ex: "가장 좋아하는 상위 3개 식단")

    public SurveyResponseDetail(SurveyResponse surveyResponse, Question question, Integer answerScore, String answerText, List<String> answerList) {
        this.surveyResponse = surveyResponse;
        this.question = question;
        this.answerScore = answerScore;
        this.answerText = answerText;
        this.answerList = answerList;
    }
}
