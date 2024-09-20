package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SurveyResponseDTO {

    private final Long surveyId; // 생성된 설문 ID
    private final Long mmId; // 월별 식단 ID
    private final LocalDateTime createdAt; // 설문 생성 날짜
    private final LocalDateTime deadlineAt; // 설문 마감 기한
    private final List<String> questions; // 설문 질문 목록

    public SurveyResponseDTO(Long surveyId, Long mmId, LocalDateTime createdAt, LocalDateTime deadlineAt, List<String> questions) {
        this.surveyId = surveyId;
        this.mmId = mmId;
        this.createdAt = createdAt;
        this.deadlineAt = deadlineAt;
        this.questions = questions;
    }
}
