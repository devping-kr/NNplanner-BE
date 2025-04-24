package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SurveyResponseDTO {

    private final Long surveyId; // 생성된 설문 ID
    private final UUID mmId; // 월별 식단 ID (UUID 타입으로 수정)
    private final LocalDateTime createdAt; // 설문 생성 날짜
    private final LocalDateTime deadlineAt; // 설문 마감 기한
    private final List<QuestionResponseDTO> questions; // 질문 목록

    @Getter
    @RequiredArgsConstructor
    public static class QuestionResponseDTO {
        private final String question;  // 질문 내용
        private final String answerType;  // 답변 형식
    }
}