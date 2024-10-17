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
import devping.nnplanner.domain.survey.repository.QuestionRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final MonthMenuRepository monthMenuRepository;
    private final QuestionRepository questionRepository;

    public SurveyService(SurveyRepository surveyRepository,
                         MonthMenuRepository monthMenuRepository,
                         SurveyResponseRepository surveyResponseRepository,
                         QuestionRepository questionRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.monthMenuRepository = monthMenuRepository;
        this.questionRepository = questionRepository;
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

        // 응답 목록을 조회
        List<SurveyResponse> responses = surveyResponseRepository.findBySurvey(survey);

        // 질문 별 만족도 분포 생성
        List<SurveyDetailResponseDTO.QuestionSatisfactionDistribution> satisfactionDistributions = new ArrayList<>();

        for (Question question : survey.getQuestions()) {
            Map<Integer, Integer> distribution;
            String questionText = question.getQuestion();
            Long questionId = question.getId();
            String answerType = question.getAnswerType();

            // 만족도 분포 생성
            distribution = switch (questionText) {
                case "월별 만족도 점수(1~10)" ->
                    createDistribution(surveyResponseRepository.getMonthlySatisfactionDistribution(surveyId));
                case "반찬 양 만족도 점수(1~10)" ->
                    createDistribution(surveyResponseRepository.getPortionSatisfactionDistribution(surveyId));
                case "위생 만족도 점수(1~10)" ->
                    createDistribution(surveyResponseRepository.getHygieneSatisfactionDistribution(surveyId));
                case "맛 만족도 점수(1~10)" ->
                    createDistribution(surveyResponseRepository.getTasteSatisfactionDistribution(surveyId));
                default -> new HashMap<>();
            };

            satisfactionDistributions.add(
                new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
                    questionId,
                    questionText,
                    distribution,
                    answerType
                )
            );
        }

        response.setSatisfactionDistributions(satisfactionDistributions);

        // 평균 점수 설정
        List<Double> avgScores = surveyResponseRepository.findAverageScores(surveyId);
        SurveyDetailResponseDTO.AverageScores averageScores = new SurveyDetailResponseDTO.AverageScores();

        // 평균 점수 리스트의 크기를 체크
        if (avgScores.size() == 4) {
            averageScores.setTotalSatisfaction(avgScores.get(0) != null ? avgScores.get(0) : 0.0);
            averageScores.setPortionSatisfaction(avgScores.get(1) != null ? avgScores.get(1) : 0.0);
            averageScores.setHygieneSatisfaction(avgScores.get(2) != null ? avgScores.get(2) : 0.0);
            averageScores.setTasteSatisfaction(avgScores.get(3) != null ? avgScores.get(3) : 0.0);
        } else {
            // 리스트의 길이가 4가 아닐 경우 기본값 설정
            averageScores.setTotalSatisfaction(0.0);
            averageScores.setPortionSatisfaction(0.0);
            averageScores.setHygieneSatisfaction(0.0);
            averageScores.setTasteSatisfaction(0.0);
        }

        response.setAverageScores(averageScores);

        // 상위 3개 메뉴 추출
        response.setLikedMenusTop3(responses.stream()
                                            .flatMap(r -> Arrays.stream(r.getLikedMenus().split(",")))
                                            .filter(menu -> !menu.trim().isEmpty()) // 빈 문자열 제거
                                            .collect(Collectors.groupingBy(menu -> menu, Collectors.counting())) // 메뉴 별 카운트
                                            .entrySet().stream()
                                            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 카운트 내림차순 정렬
                                            .limit(3) // 상위 3개
                                            .map(e -> new MenuSelectionResponseDTO(LocalDateTime.now(), e.getKey())) // MenuSelectionResponseDTO로 변환
                                            .collect(Collectors.toList()));

        response.setDislikedMenusTop3(responses.stream()
                                               .flatMap(r -> Arrays.stream(r.getDislikedMenus().split(",")))
                                               .filter(menu -> !menu.trim().isEmpty()) // 빈 문자열 제거
                                               .collect(Collectors.groupingBy(menu -> menu, Collectors.counting())) // 메뉴 별 카운트
                                               .entrySet().stream()
                                               .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 카운트 내림차순 정렬
                                               .limit(3) // 상위 3개
                                               .map(e -> new MenuSelectionResponseDTO(LocalDateTime.now(), e.getKey())) // MenuSelectionResponseDTO로 변환
                                               .collect(Collectors.toList()));

        response.setDesiredMenus(responses.stream()
                                          .flatMap(r -> r.getDesiredMenus().stream())
                                          .collect(Collectors.toList()));

        response.setMessagesToDietitian(responses.stream()
                                                 .map(SurveyResponse::getMessagesToDietitian)
                                                 .filter(Objects::nonNull) // null 값 제거
                                                 .distinct() // 중복 제거
                                                 .collect(Collectors.toList()));

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

        // 응답을 처리하기 위한 반복문
        for (SurveyResponseRequestDTO.ResponseDTO response : surveyResponseRequestDTO.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId())
                                                  .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND)); // 질문 객체 조회

            // likedMenusTop3와 dislikedMenusTop3를 문자열로 변환
            String likedMenus = response.getLikedMenusTop3() != null ?
                response.getLikedMenusTop3().stream()
                        .map(MenuSelectionResponseDTO::getMenu)
                        .collect(Collectors.joining(",")) : "";

            String dislikedMenus = response.getDislikedMenusTop3() != null ?
                response.getDislikedMenusTop3().stream()
                        .map(MenuSelectionResponseDTO::getMenu)
                        .collect(Collectors.joining(",")) : "";

            // desiredMenu를 List<String>으로 변환
            List<String> desiredMenus = response.getDesiredMenu() != null ?
                List.of(response.getDesiredMenu()) :
                List.of();

            // SurveyResponse 객체 생성
            SurveyResponse surveyResponse = new SurveyResponse(
                survey,
                question,
                likedMenus,
                dislikedMenus,
                desiredMenus,
                response.getMessageToDietitian(),
                response.getMonthlySatisfaction(),
                response.getPortionSatisfaction(),
                response.getHygieneSatisfaction(),
                response.getTasteSatisfaction(),
                LocalDateTime.now()
            );

            // surveyResponse 객체 저장
            surveyResponseRepository.save(surveyResponse);

            // 로그 추가
            System.out.println("Saved response: " + surveyResponse);
        }

        // 가장 최근에 저장된 응답을 반환
        SurveyResponse lastResponse = surveyResponseRepository.findTopBySurveyOrderByResponseDateDesc(survey);
        return new SurveyResponseResponseDTO(lastResponse.getId(), survey.getId(), lastResponse.getResponseDate());
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
