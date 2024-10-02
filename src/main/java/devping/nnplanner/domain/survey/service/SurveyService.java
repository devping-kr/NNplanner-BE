package devping.nnplanner.domain.survey.service;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.response.SurveyResponseDTO;
import devping.nnplanner.domain.survey.entity.Question;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final MonthMenuRepository monthMenuRepository;

    public SurveyService(SurveyRepository surveyRepository, MonthMenuRepository monthMenuRepository) {
        this.surveyRepository = surveyRepository;
        this.monthMenuRepository = monthMenuRepository;
    }

    public SurveyResponseDTO createSurvey(UUID mmId, SurveyRequestDTO requestDTO) {
        // 월별 식단(MonthMenu)을 ID로 조회
        MonthMenu monthMenu = monthMenuRepository.findById(mmId)
                                                 .orElseThrow(() -> new CustomException(ErrorCode.MONTH_MENU_NOT_FOUND));

        // 마감 기한이 null이면 기본값(현재 시점으로부터 2주 뒤) 설정
        LocalDateTime deadline = requestDTO.getDeadlineAt() != null ? requestDTO.getDeadlineAt() : LocalDateTime.now().plusWeeks(2);

        // 예외 처리: 마감 기한이 현재보다 이전일 경우
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SURVEY_DEADLINE);
        }

        // 필수 질문 추가
        List<Question> allQuestions = new ArrayList<>(getMandatoryQuestions());

        // 추가 질문이 있으면 처리
        if (requestDTO.getAdditionalQuestions() != null) {
            requestDTO.getAdditionalQuestions().forEach(q ->
                allQuestions.add(new Question(q.getQuestion(), q.getAnswerType()))
            );
        }

        // 설문 생성 (MonthMenu와 연관)
        Survey survey = new Survey(monthMenu, deadline, allQuestions);

        Survey savedSurvey = surveyRepository.save(survey);

        // 응답 DTO 생성
        List<SurveyResponseDTO.QuestionResponseDTO> responseQuestions
            = savedSurvey.getQuestions().stream()
                         .map(q -> new SurveyResponseDTO.QuestionResponseDTO(q.getQuestion(), q.getAnswerType()))
                         .collect(Collectors.toList());

        return new SurveyResponseDTO(
            savedSurvey.getId(),
            savedSurvey.getMonthMenu().getMonthMenuId(),
            savedSurvey.getCreatedAt(),
            savedSurvey.getDeadlineAt(),
            responseQuestions
        );
    }

    // 필수 질문을 반환하는 메서드
    private List<Question> getMandatoryQuestions() {
        return List.of(
            new Question("월별 만족도 점수(1~10)", "radio"),
            new Question("반찬 양 만족도 점수(1~10)", "radio"),
            new Question("위생 만족도 점수(1~10)", "radio"),
            new Question("맛 만족도 점수(1~10)", "radio"),
            new Question("가장 좋아하는 상위 3개 식단", "text"),
            new Question("가장 싫어하는 상위 3개 식단", "text"),
            new Question("먹고 싶은 메뉴", "text"),
            new Question("영양사에게 한마디", "text")
        );
    }
}
