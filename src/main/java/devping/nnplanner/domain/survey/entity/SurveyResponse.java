package devping.nnplanner.domain.survey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    private LocalDateTime responseDate;

    // 응답에 포함된 질문 응답 세부 정보
    @OneToMany(mappedBy = "surveyResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyResponseDetail> responseDetails;

    public SurveyResponse(Survey survey, LocalDateTime responseDate) {
        this.survey = survey;
        this.responseDate = responseDate;
        this.responseDetails = new ArrayList<>(); // 초기화
    }


    // responseDetails 리스트를 추가하기 위한 메서드
    public void addResponseDetail(SurveyResponseDetail detail) {
        responseDetails.add(detail);
        detail.setSurveyResponse(this);
    }
}
