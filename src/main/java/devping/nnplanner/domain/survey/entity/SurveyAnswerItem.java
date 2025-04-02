package devping.nnplanner.domain.survey.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SurveyAnswerItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_response_detail_id", nullable = false)
    private SurveyResponseDetail surveyResponseDetail;

    @Column(name = "answer_item", length = 1000)
    private String answerItem;

    @Column(name = "answer_score")
    private Integer answerScore;  // radio 타입 점수 저장

    @Enumerated(EnumType.STRING)
    private AnswerItemType answerItemType;

    @Builder
    private SurveyAnswerItem(Long id, SurveyResponseDetail surveyResponseDetail, String answerItem, Integer answerScore, AnswerItemType answerItemType) {
        this.id = id;
        this.surveyResponseDetail = surveyResponseDetail;
        this.answerItem = answerItem;
        this.answerScore = answerScore;
        this.answerItemType = answerItemType;
    }
}
