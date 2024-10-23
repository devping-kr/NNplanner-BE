package devping.nnplanner.domain.survey.controller;

import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyResponseRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.response.*;
import devping.nnplanner.domain.survey.entity.SurveyState;
import devping.nnplanner.domain.survey.service.SurveyService;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<ApiResponse<SurveyResponseDTO>> createSurvey(
        @RequestBody @Valid SurveyRequestDTO requestDTO,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        SurveyResponseDTO responseDTO = surveyService.createSurvey(requestDTO, userDetails);

        return GlobalResponse.CREATED("설문 생성 성공", responseDTO);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SurveyListResponseDTO>> getSurveys(
        @RequestParam(value = "startDate", required = false) String startDateStr,
        @RequestParam(value = "endDate", required = false) String endDateStr,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "state", required = false) SurveyState state
    ) {
        SurveyListResponseDTO responseDTO
            = surveyService.getSurveys(startDateStr, endDateStr, sort, page, pageSize, search, state);

        return GlobalResponse.OK("설문 목록 조회 성공", responseDTO);
    }

    @GetMapping("/{surveyId}")
    public ResponseEntity<ApiResponse<SurveyDetailResponseDTO>> getSurveyDetail(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable("surveyId") Long surveyId) {

        SurveyDetailResponseDTO responseDTO = surveyService.getSurveyDetail(userDetails, surveyId);

        return GlobalResponse.OK("설문 상세 조회 성공", responseDTO);
    }

    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<ApiResponse<SurveyResponseResponseDTO>> submitSurveyResponse(
        @PathVariable Long surveyId,
        @RequestBody @Valid SurveyResponseRequestDTO surveyResponseRequestDTO) {

        SurveyResponseResponseDTO responseDTO = surveyService.submitSurveyResponse(surveyId, surveyResponseRequestDTO);

        return GlobalResponse.CREATED("설문 응답 성공", responseDTO);
    }

    @PutMapping("/{surveyId}")
    public ResponseEntity<ApiResponse<SurveyUpdateResponseDTO>> updateSurvey(
        @PathVariable Long surveyId,
        @RequestBody @Valid SurveyUpdateRequestDTO requestDTO) {

        SurveyUpdateResponseDTO responseDTO = surveyService.updateSurvey(surveyId, requestDTO);

        return GlobalResponse.OK("설문 수정 성공", responseDTO);
    }

    @DeleteMapping("/{surveyId}")
    public ResponseEntity<ApiResponse<Void>> deleteSurvey(
        @PathVariable Long surveyId) {

        surveyService.deleteSurvey(surveyId);

        return GlobalResponse.NO_CONTENT();
    }
}
