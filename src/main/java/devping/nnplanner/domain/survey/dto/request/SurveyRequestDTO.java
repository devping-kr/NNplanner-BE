package devping.nnplanner.domain.survey.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SurveyRequestDTO {

    private final Long mmId; // 월별 식단 ID
    private String surveyName;
    private final LocalDateTime deadlineAt; // 설문 마감 기한
    private final List<AdditionalQuestionDTO> additionalQuestions; // 추가 질문 목록

    @Getter
    @RequiredArgsConstructor
    public static class AdditionalQuestionDTO {
        private final String question;  // 질문 내용
        private final String answerType;  // 답변 형식
    }
}