package devping.nnplanner.domain.survey.controller;

import devping.nnplanner.domain.survey.dto.response.SurveyListResponseDTO;
import devping.nnplanner.domain.survey.service.SurveyService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping
    public ResponseEntity<ApiResponse<SurveyListResponseDTO>> getSurveys(
        @RequestParam(value = "startDate", required = false) String startDateStr,
        @RequestParam(value = "endDate", required = false) String endDateStr,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
        @RequestParam(value = "search", required = false) String search
    ) {
        SurveyListResponseDTO responseDTO = surveyService.getSurveys(startDateStr, endDateStr, sort, page, pageSize, search);

        return GlobalResponse.OK("설문 목록 조회 성공", responseDTO);
    }
}
