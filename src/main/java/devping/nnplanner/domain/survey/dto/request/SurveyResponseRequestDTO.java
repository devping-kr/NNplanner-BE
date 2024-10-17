package devping.nnplanner.domain.survey.dto.request;

import devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO;
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

        private Integer satisfactionScore; // 만족도 점수 (1~10) - radio 타입의 질문에만 사용
        private String textAnswer; // 텍스트 응답 - text 타입의 질문에 사용

        private List<MenuSelectionResponseDTO> likedMenusTop3; // 가장 좋아하는 메뉴 상위 3개
        private List<MenuSelectionResponseDTO> dislikedMenusTop3; // 가장 싫어하는 메뉴 상위 3개
        private List<String> desiredMenus; // 먹고 싶은 메뉴 (선택 사항)
        private String messageToDietitian; // 영양사에게 남기는 메시지 (선택 사항)
    }

}