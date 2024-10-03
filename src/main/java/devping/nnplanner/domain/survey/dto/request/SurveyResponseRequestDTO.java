package devping.nnplanner.domain.survey.dto.request;

import devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyResponseRequestDTO {

    @Min(1)
    @Max(10)
    @NotNull
    private Integer monthlySatisfaction;

    @Min(1)
    @Max(10)
    @NotNull
    private Integer portionSatisfaction;

    @Min(1)
    @Max(10)
    @NotNull
    private Integer hygieneSatisfaction;

    @Min(1)
    @Max(10)
    @NotNull
    private Integer tasteSatisfaction;

    private String desiredMenu;       // 먹고 싶은 메뉴 (선택 사항)
    private String messageToDietitian; // 영양사에게 남기는 메시지 (선택 사항)
    private List<MenuSelectionResponseDTO> likedMenusTop3;  // 가장 좋아하는 메뉴 상위 3개
    private List<MenuSelectionResponseDTO> dislikedMenusTop3;  // 가장 싫어하는 메뉴 상위 3개
    private List<String> additionalAnswers;
}