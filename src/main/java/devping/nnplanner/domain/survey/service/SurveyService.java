package devping.nnplanner.domain.survey.service;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.response.MenuSelectionResponseDTO;
import devping.nnplanner.domain.survey.dto.response.SurveyDetailResponseDTO;
import devping.nnplanner.domain.survey.dto.response.SurveyListResponseDTO;
import devping.nnplanner.domain.survey.dto.response.SurveyResponseDTO;
import devping.nnplanner.domain.survey.entity.Question;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.domain.survey.repository.SurveyResponseRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final MonthMenuRepository monthMenuRepository;

    public SurveyService(SurveyRepository surveyRepository,
                         MonthMenuRepository monthMenuRepository, SurveyResponseRepository surveyResponseRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyResponseRepository = surveyResponseRepository;
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
        Survey survey = new Survey(monthMenu, requestDTO.getSurveyName(), deadline, allQuestions);

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

    @Transactional(readOnly = true)
    public SurveyListResponseDTO getSurveys(String startDateStr, String endDateStr, String sort, int page, int pageSize, String search) {
        // 기본 정렬 기준 설정
        String defaultSort = "createdAt";  // 기본적으로 createdAt 필드로 정렬
        Sort.Direction defaultDirection = Sort.Direction.DESC;  // 기본 정렬 방향은 내림차순

        // sort 파라미터 처리
        String sortField = defaultSort;
        Sort.Direction sortDirection = defaultDirection;

        if (sort != null && !sort.trim().isEmpty()) {
            String[] sortParams = sort.split(",");
            sortField = sortParams[0];  // 필드 이름 추출
            if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")) {
                sortDirection = Sort.Direction.ASC;  // 정렬 방향이 asc이면 오름차순으로 설정
            } else {
                sortDirection = Sort.Direction.DESC;  // 그렇지 않으면 내림차순
            }
        }

        // Pageable 객체 생성 시 동적으로 정렬 기준 적용
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(sortDirection, sortField));

        // 날짜 문자열을 LocalDateTime으로 변환
        LocalDateTime startDate = LocalDateTime.now().minusYears(1); // 기본 시작일은 1년 전
        LocalDateTime endDate = LocalDateTime.now(); // 기본 종료일은 현재 시점
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = LocalDateTime.parse(startDateStr, formatter);
        }

        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = LocalDateTime.parse(endDateStr, formatter);
        }

        // search가 null이거나 빈 값일 때 기본값 설정
        String searchValue = (search == null || search.trim().isEmpty()) ? "" : search;

        // 쿼리 실행
        Page<Survey> surveyPage = surveyRepository.findSurveys(
            searchValue,
            startDate,
            endDate,
            pageable
        );

        // 결과 매핑
        List<SurveyListResponseDTO.SurveyItemResponseDTO> surveys
            = surveyPage.stream()
                        .map(survey -> new SurveyListResponseDTO.SurveyItemResponseDTO(
                            survey.getId(),
                            survey.getSurveyName(),
                            survey.getCreatedAt(),
                            survey.getDeadlineAt(),
                            survey.getState().toString()
                        ))
                        .collect(Collectors.toList());

        return new SurveyListResponseDTO(surveyPage.getTotalElements(), page, surveyPage.getTotalPages(), surveys);
    }

    @Transactional(readOnly = true)
    public SurveyDetailResponseDTO getSurveyDetail(Long surveyId) {
        // 설문 조회
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        SurveyDetailResponseDTO response = new SurveyDetailResponseDTO();
        response.setSurveyName(survey.getSurveyName());

        // 상위 3개의 좋아한 메뉴
        List<MenuSelectionResponseDTO> likedMenus = surveyResponseRepository.findTopLikedMenus(surveyId);
        if (likedMenus.isEmpty()) {
            response.setLikedMenusTop3(List.of());
        } else {
            response.setLikedMenusTop3(likedMenus);
        }

        // 상위 3개의 싫어한 메뉴
        List<MenuSelectionResponseDTO> dislikedMenus = surveyResponseRepository.findTopDislikedMenus(surveyId);
        if (dislikedMenus.isEmpty()) {
            response.setDislikedMenusTop3(List.of());
        } else {
            response.setDislikedMenusTop3(dislikedMenus);
        }

        // 원하는 메뉴
        List<String> desiredMenus = surveyResponseRepository.findDesiredMenus(surveyId);
        if (desiredMenus.isEmpty()) {
            response.setDesiredMenus(List.of()); // 응답이 없을 경우 빈 리스트 설정
        } else {
            response.setDesiredMenus(desiredMenus);
        }

        // 영양사에게 한마디
        List<String> messagesToDietitian = surveyResponseRepository.findMessagesToDietitian(surveyId);
        if (messagesToDietitian.isEmpty()) {
            response.setMessagesToDietitian(List.of()); // 응답이 없을 경우 빈 리스트 설정
        } else {
            response.setMessagesToDietitian(messagesToDietitian);
        }

        // 만족도 분포
        Map<Integer, Long> distribution = surveyResponseRepository.getSatisfactionDistribution(surveyId);
        Map<String, Integer> satisfactionDistribution = distribution.entrySet()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(
                                                                        entry -> String.valueOf(entry.getKey()),
                                                                        entry -> entry.getValue().intValue()
                                                                    ));
        response.setSatisfactionDistribution(satisfactionDistribution);

        // 평균 점수 설정
        Object[] avgScores = surveyResponseRepository.findAverageScores(surveyId);
        if (avgScores != null && avgScores.length == 4) {
            SurveyDetailResponseDTO.AverageScores averageScores = new SurveyDetailResponseDTO.AverageScores();
            averageScores.setTotalSatisfaction(avgScores[0] != null ? ((Number) avgScores[0]).doubleValue() : 0.0);
            averageScores.setPortionSatisfaction(avgScores[1] != null ? ((Number) avgScores[1]).doubleValue() : 0.0);
            averageScores.setHygieneSatisfaction(avgScores[2] != null ? ((Number) avgScores[2]).doubleValue() : 0.0);
            averageScores.setTasteSatisfaction(avgScores[3] != null ? ((Number) avgScores[3]).doubleValue() : 0.0);
            response.setAverageScores(averageScores);
        } else {
            response.setAverageScores(new SurveyDetailResponseDTO.AverageScores()); // 기본 값 설정
        }

        return response;
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
