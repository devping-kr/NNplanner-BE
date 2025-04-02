package devping.nnplanner.domain.survey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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



    @Builder
    private SurveyResponseDetail(SurveyResponse surveyResponse, Question question) {
        this.surveyResponse = surveyResponse;
        this.question = question;
    }
}
