package devping.nnplanner.domain.survey.controller;

import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.response.SurveyResponseDTO;
import devping.nnplanner.domain.survey.service.SurveyService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SurveyResponseDTO>> createSurvey(
        @RequestBody @Valid SurveyRequestDTO requestDTO) {

        SurveyResponseDTO responseDTO = surveyService.createSurvey(requestDTO);

        return GlobalResponse.CREATED("설문 생성 성공", responseDTO);
    }
}
