package devping.nnplanner.domain.survey.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SurveyRequestDTO {

    private Long mmId; // 월별 식단 ID
    private LocalDateTime deadlineAt; // 설문 마감 기한
    private List<String> additionalQuestions; // 추가 질문 목록

    // 생성자 추가
    public SurveyRequestDTO(Long mmId, LocalDateTime deadlineAt, List<String> additionalQuestions) {
        this.mmId = mmId;
        this.deadlineAt = deadlineAt;
        this.additionalQuestions = additionalQuestions;
    }
}