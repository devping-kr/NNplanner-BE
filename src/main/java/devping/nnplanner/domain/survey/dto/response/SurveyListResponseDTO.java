package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SurveyListResponseDTO {

    private long totalItems;
    private int currentPage;
    private int totalPages;
    private List<SurveyItemResponseDTO> surveys;

    // 생성자
    public SurveyListResponseDTO(long totalItems, int currentPage, int totalPages, List<SurveyItemResponseDTO> surveys) {
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.surveys = surveys;
    }

    // 설문 항목에 대한 내부 클래스
    @Getter
    @Setter
    public static class SurveyItemResponseDTO {
        private Long surveyId;
        private String surveyName;
        private LocalDateTime createdAt;
        private LocalDateTime deadlineAt;
        private String state;

        // 생성자
        public SurveyItemResponseDTO(Long surveyId, String surveyName, LocalDateTime createdAt, LocalDateTime deadlineAt, String state) {
            this.surveyId = surveyId;
            this.surveyName = surveyName;
            this.createdAt = createdAt;
            this.deadlineAt = deadlineAt;
            this.state = state;
        }
    }
}

