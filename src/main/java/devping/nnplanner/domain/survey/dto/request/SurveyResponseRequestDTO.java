package devping.nnplanner.domain.survey.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SurveyResponseRequestDTO {

    private List<SatisfactionScoreDTO> satisfactionScore;
    private List<MenuSelectionDTO> likedMenusTop3;
    private List<MenuSelectionDTO> dislikedMenusTop3;
    private List<MenuSelectionDTO> desiredMenus;
    private List<MessageToDietitianDTO> messageToDietitian;

    @Getter
    @Setter
    public static class SatisfactionScoreDTO {
        private Long questionId; // 만족도 질문 ID
        private LocalDateTime responseDate;
        private Integer score; // 만족도 점수 (1~10)
    }

    @Getter
    @Setter
    public static class MenuSelectionDTO {
        private Long questionId; // 메뉴 질문 ID
        private LocalDateTime responseDate; // 응답 날짜
        private List<String> menus;  // 메뉴 리스트
    }

    @Getter
    @Setter
    public static class MessageToDietitianDTO {
        private Long questionId; // 메시지 질문 ID
        private LocalDateTime responseDate; // 응답 날짜
        private String message; // 영양사에게 남기는 메시지
    }
}