package devping.nnplanner.domain.survey.service;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.survey.dto.request.QuestionUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyResponseRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.response.*;
import devping.nnplanner.domain.survey.entity.Question;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.entity.SurveyResponse;
import devping.nnplanner.domain.survey.entity.SurveyState;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public SurveyResponseDTO createSurvey(SurveyRequestDTO requestDTO) {
        // 월별 식단(MonthMenu)을 ID로 조회
        MonthMenu monthMenu = monthMenuRepository.findById(requestDTO.getMmId())
                                                 .orElseThrow(() -> new CustomException(ErrorCode.MONTH_MENU_NOT_FOUND));

        // 마감 기한 설정
        LocalDateTime deadline = requestDTO.getDeadlineAt() != null ? requestDTO.getDeadlineAt() : LocalDateTime.now().plusWeeks(2);

        // 예외 처리: 마감 기한이 현재보다 이전일 경우
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SURVEY_DEADLINE);
        }

        // 설문 객체 생성
        Survey survey = new Survey(monthMenu, requestDTO.getSurveyName(), deadline, new ArrayList<>());

        // 기본 질문 추가
        getMandatoryQuestions().forEach(q -> {
            Question question = new Question(q.getQuestion(), q.getAnswerType(), true, survey);
            survey.addQuestion(question);
        });

        // 추가 질문 처리
        if (requestDTO.getAdditionalQuestions() != null) {
            requestDTO.getAdditionalQuestions().forEach(q ->
                survey.addQuestion(new Question(q.getQuestion(), q.getAnswerType(), false, survey))
            );
        }

        // 설문 저장
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
    public SurveyListResponseDTO getSurveys(String startDateStr, String endDateStr,
                                            String sort, int page, int pageSize, String search, SurveyState state) {
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
            state,
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
                            survey.getState().toString()))
                        .collect(Collectors.toList());

        return new SurveyListResponseDTO(surveyPage.getTotalElements(), page, surveyPage.getTotalPages(), surveys);
    }

    @Transactional(readOnly = true)
    public SurveyDetailResponseDTO getSurveyDetail(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        SurveyDetailResponseDTO response = new SurveyDetailResponseDTO();
        response.setSurveyName(survey.getSurveyName());

        List<SurveyDetailResponseDTO.QuestionSatisfactionDistribution> satisfactionDistributions = new ArrayList<>();

        // 각 질문별 분포 생성
        satisfactionDistributions.add(new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
            null, "월별 만족도 점수(1~10)", createDistribution(surveyResponseRepository.getMonthlySatisfactionDistribution(surveyId))
        ));
        satisfactionDistributions.add(new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
            null, "반찬 양 만족도 점수(1~10)", createDistribution(surveyResponseRepository.getPortionSatisfactionDistribution(surveyId))
        ));
        satisfactionDistributions.add(new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
            null, "위생 만족도 점수(1~10)", createDistribution(surveyResponseRepository.getHygieneSatisfactionDistribution(surveyId))
        ));
        satisfactionDistributions.add(new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
            null, "맛 만족도 점수(1~10)", createDistribution(surveyResponseRepository.getTasteSatisfactionDistribution(surveyId))
        ));

        response.setSatisfactionDistributions(satisfactionDistributions);

        Object[] avgScores = surveyResponseRepository.findAverageScores(surveyId);
        SurveyDetailResponseDTO.AverageScores averageScores = new SurveyDetailResponseDTO.AverageScores();

        if (avgScores != null && avgScores.length == 4) {
            averageScores.setTotalSatisfaction(avgScores[0] instanceof Number ? ((Number) avgScores[0]).doubleValue() : 0.0);
            averageScores.setPortionSatisfaction(avgScores[1] instanceof Number ? ((Number) avgScores[1]).doubleValue() : 0.0);
            averageScores.setHygieneSatisfaction(avgScores[2] instanceof Number ? ((Number) avgScores[2]).doubleValue() : 0.0);
            averageScores.setTasteSatisfaction(avgScores[3] instanceof Number ? ((Number) avgScores[3]).doubleValue() : 0.0);
        } else {
            // 평균 점수가 없을 경우 기본값 0.0 설정
            averageScores.setTotalSatisfaction(0.0);
            averageScores.setPortionSatisfaction(0.0);
            averageScores.setHygieneSatisfaction(0.0);
            averageScores.setTasteSatisfaction(0.0);
        }

        response.setAverageScores(averageScores);


        return response;
    }


    private Map<Integer, Integer> createDistribution(List<Object[]> distributionData) {
        Map<Integer, Integer> distributionMap = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            distributionMap.put(i, 0);
        }
        for (Object[] result : distributionData) {
            Integer score = (Integer) result[0];
            Long count = (Long) result[1];
            distributionMap.put(score, count.intValue());
        }
        return distributionMap;
    }


    @Transactional
    public SurveyResponseResponseDTO submitSurveyResponse(Long surveyId, SurveyResponseRequestDTO surveyResponseRequestDTO) {
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        // likedMenusTop3와 dislikedMenusTop3를 문자열로 변환
        String likedMenus = surveyResponseRequestDTO.getLikedMenusTop3().stream()
                                                    .map(MenuSelectionResponseDTO::getMenu)
                                                    .collect(Collectors.joining(","));
        String dislikedMenus = surveyResponseRequestDTO.getDislikedMenusTop3().stream()
                                                       .map(MenuSelectionResponseDTO::getMenu)
                                                       .collect(Collectors.joining(","));

        // desiredMenu를 List<String>으로 변환
        List<String> desiredMenus = surveyResponseRequestDTO.getDesiredMenu() != null ?
            List.of(surveyResponseRequestDTO.getDesiredMenu()) :
            List.of();

        // SurveyResponse 객체 생성
        SurveyResponse surveyResponse = new SurveyResponse(
            survey,
            likedMenus,
            dislikedMenus,
            desiredMenus,
            surveyResponseRequestDTO.getMessageToDietitian(),
            surveyResponseRequestDTO.getMonthlySatisfaction(),
            surveyResponseRequestDTO.getPortionSatisfaction(),
            surveyResponseRequestDTO.getHygieneSatisfaction(),
            surveyResponseRequestDTO.getTasteSatisfaction(),
            LocalDateTime.now()
        );

        // surveyResponse 객체 저장
        surveyResponseRepository.save(surveyResponse);

        return new SurveyResponseResponseDTO(surveyResponse.getId(), survey.getId(), surveyResponse.getResponseDate());
    }

    public void deleteSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        surveyRepository.delete(survey);
    }


    // 필수 질문을 반환하는 메서드
    private List<Question> getMandatoryQuestions() {
        return List.of(
            new Question("월별 만족도 점수(1~10)", "radio", true, null),
            new Question("반찬 양 만족도 점수(1~10)", "radio", true, null),
            new Question("위생 만족도 점수(1~10)", "radio", true, null),
            new Question("맛 만족도 점수(1~10)", "radio", true, null),
            new Question("가장 좋아하는 상위 3개 식단", "text", true, null),
            new Question("가장 싫어하는 상위 3개 식단", "text", true, null),
            new Question("먹고 싶은 메뉴", "text", true, null),
            new Question("영양사에게 한마디", "text", true, null)
        );
    }

    @Transactional
    public SurveyUpdateResponseDTO updateSurvey(Long surveyId, SurveyUpdateRequestDTO requestDTO) {
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        List<SurveyUpdateResponseDTO.QuestionResponseDTO> updatedQuestions = new ArrayList<>();

        // 설문 이름 수정
        if (requestDTO.getSurveyName() != null && !requestDTO.getSurveyName().isEmpty()) {
            survey.setSurveyName(requestDTO.getSurveyName());
        }

        // 마감 기한 수정
        if (requestDTO.getDeadlineAt() != null && !requestDTO.getDeadlineAt().isBefore(LocalDateTime.now())) {
            survey.setDeadlineAt(requestDTO.getDeadlineAt());
        }

        // 설문 상태 수정
        if (requestDTO.getState() != null) {
            survey.setState(requestDTO.getState());
        }

        // 추가 질문 수정 (기본 질문은 수정하지 않음)
        if (requestDTO.getQuestions() != null && !requestDTO.getQuestions().isEmpty()) {
            for (QuestionUpdateRequestDTO questionUpdateRequest : requestDTO.getQuestions()) {
                Question question = survey.getQuestions().stream()
                                          .filter(q -> q.getId().equals(questionUpdateRequest.getQuestionId()) && !q.isMandatory())
                                          .findFirst()
                                          .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

                question.setQuestion(questionUpdateRequest.getQuestion());
                updatedQuestions.add(new SurveyUpdateResponseDTO.QuestionResponseDTO(question.getId(), LocalDateTime.now()));
            }
        }

        surveyRepository.save(survey);

        return new SurveyUpdateResponseDTO(
            survey.getId(),
            survey.getSurveyName(),
            survey.getDeadlineAt(),
            survey.getState(),
            updatedQuestions
        );
    }

}
