package devping.nnplanner.domain.survey.dto.request;

import devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyResponseRequestDTO {

    private List<ResponseDTO> responses;

    @Getter
    @Setter
    public static class ResponseDTO {
        @NotNull
        private Long questionId; // 질문 ID

        @Min(1)
        @Max(10)
        @NotNull
        private Integer monthlySatisfaction; // 월별 만족도 점수 (1~10)

        @Min(1)
        @Max(10)
        @NotNull
        private Integer portionSatisfaction; // 반찬 양 만족도 점수 (1~10)

        @Min(1)
        @Max(10)
        @NotNull
        private Integer hygieneSatisfaction; // 위생 만족도 점수 (1~10)

        @Min(1)
        @Max(10)
        @NotNull
        private Integer tasteSatisfaction; // 맛 만족도 점수 (1~10)

        private String desiredMenu; // 먹고 싶은 메뉴 (선택 사항)
        private String messageToDietitian; // 영양사에게 남기는 메시지 (선택 사항)

        private List<MenuSelectionResponseDTO> likedMenusTop3; // 가장 좋아하는 메뉴 상위 3개
        private List<MenuSelectionResponseDTO> dislikedMenusTop3; // 가장 싫어하는 메뉴 상위 3개
        private List<String> additionalAnswers; // 추가 답변 (선택 사항)
    }
}