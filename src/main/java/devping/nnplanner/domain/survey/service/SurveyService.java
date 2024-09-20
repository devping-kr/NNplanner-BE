package devping.nnplanner.domain.survey.service;

import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.response.SurveyResponseDTO;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    public SurveyResponseDTO createSurvey(SurveyRequestDTO requestDTO) {
        // 마감 기한이 null이면 기본값(현재 시점으로부터 2주 뒤) 설정
        LocalDateTime deadline = requestDTO.getDeadlineAt() != null ? requestDTO.getDeadlineAt() : LocalDateTime.now().plusWeeks(2);

        // 예외 처리: 마감 기한이 현재보다 이전일 경우
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SURVEY_DEADLINE);
        }

        // 필수 질문 하드코딩
        List<String> mandatoryQuestions = List.of(
            "월별 만족도 점수(1~10)",
            "반찬 양 만족도 점수(1~10)",
            "위생 만족도 점수(1~10)",
            "맛 만족도 점수(1~10)",
            "가장 좋아하는 상위 3개 식단",
            "가장 싫어하는 상위 3개 식단",
            "먹고 싶은 메뉴",
            "영양사에게 한마디"
        );

        // 필수 질문을 저장할 리스트 생성
        List<String> allQuestions = new ArrayList<>(mandatoryQuestions); // 필수 질문 추가

        // 사용자가 추가 질문을 입력했다면 추가
        if (requestDTO.getAdditionalQuestions() != null) {
            allQuestions.addAll(requestDTO.getAdditionalQuestions()); // 추가 질문 추가
        }

        // 설문 생성
        Survey survey = new Survey(
            requestDTO.getMmId(),
            deadline,  // 수정된 마감 기한 사용
            allQuestions // 필수 + 추가 질문
        );

        Survey savedSurvey = surveyRepository.save(survey);

        // 응답 DTO 생성
        return new SurveyResponseDTO(
            savedSurvey.getId(),
            savedSurvey.getMmId(),
            savedSurvey.getCreatedAt(),
            savedSurvey.getDeadlineAt(),
            savedSurvey.getQuestions() // 모든 질문을 반환
        );
    }
}
