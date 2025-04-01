package devping.nnplanner.domain.survey.service;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.survey.dto.request.QuestionUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyResponseRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.response.*;
import devping.nnplanner.domain.survey.entity.*;
import devping.nnplanner.domain.survey.repository.QuestionRepository;
import devping.nnplanner.domain.survey.repository.ResponseDetailRepository;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.domain.survey.repository.SurveyResponseRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Stream;

@Service
@Transactional
public class SurveyService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyService.class);

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final MonthMenuRepository monthMenuRepository;
    private final QuestionRepository questionRepository;
    private final ResponseDetailRepository surveyResponseDetailRepository;

    public SurveyService(SurveyRepository surveyRepository, SurveyResponseRepository surveyResponseRepository, MonthMenuRepository monthMenuRepository, QuestionRepository questionRepository, ResponseDetailRepository surveyResponseDetailRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.monthMenuRepository = monthMenuRepository;
        this.questionRepository = questionRepository;
        this.surveyResponseDetailRepository = surveyResponseDetailRepository;
    }

    @Transactional
    public SurveyResponseDTO createSurvey(SurveyRequestDTO requestDTO, UserDetailsImpl userDetails) {
        UUID mmId = requestDTO.getMmId();
        logger.debug("Requested mmId: {}", mmId);

        // 월별 식단(MonthMenu)을 ID로 조회
        MonthMenu monthMenu = monthMenuRepository.findById(mmId)
                                                 .orElseThrow(() -> {
                                                     logger.error("MonthMenu not found for ID: {}", mmId);
                                                     return new CustomException(ErrorCode.MONTH_MENU_NOT_FOUND);
                                                 });


        // 마감 기한 설정
        LocalDateTime deadline = requestDTO.getDeadlineAt() != null ? requestDTO.getDeadlineAt() : LocalDateTime.now().plusWeeks(2);

        // 예외 처리: 마감 기한이 현재보다 이전일 경우
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SURVEY_DEADLINE);
        }

        // 설문 객체 생성 (userId 포함)
        Survey survey = new Survey(monthMenu, requestDTO.getSurveyName(), deadline, new ArrayList<>());
        survey.setUser(userDetails.getUser());

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
    public SurveyListResponseDTO getSurveys(UserDetailsImpl userDetails, String startDateStr, String endDateStr,
                                            String sort, int page, int pageSize, String search, SurveyState state) {
        // 기본 정렬 설정
        String defaultSort = "createdAt";
        Sort.Direction defaultDirection = Sort.Direction.DESC;

        String sortField = (sort != null && !sort.trim().isEmpty()) ? sort.split(",")[0] : defaultSort;
        Sort.Direction sortDirection = (sort != null && sort.endsWith("asc")) ? Sort.Direction.ASC : defaultDirection;

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(sortDirection, sortField));

        // 날짜 처리
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startDate = (startDateStr != null && !startDateStr.isEmpty()) ? LocalDateTime.parse(startDateStr, formatter) : LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDateTime.parse(endDateStr, formatter) : LocalDateTime.now();

        String searchValue = (search == null || search.trim().isEmpty()) ? "" : search;

        // 현재 로그인한 사용자의 userId를 가져옴
        Long userId = userDetails.getUser().getUserId();

        // 쿼리 실행
        Page<Survey> surveyPage = surveyRepository.findSurveys(
            userId,  // userId 전달
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
    public SurveyDetailResponseDTO getSurveyDetail(UserDetailsImpl userDetails, Long surveyId) {
        Long userId = userDetails.getUser().getUserId();

        // 로그인한 사용자가 생성한 설문만 조회
        Survey survey = surveyRepository.findByIdAndUser_UserId(surveyId, userId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        // 설문 상세 응답 DTO 구성
        SurveyDetailResponseDTO response = new SurveyDetailResponseDTO();
        response.setSurveyName(survey.getSurveyName());
        response.setDeadline(survey.getDeadlineAt());
        response.setMmId(survey.getMonthMenu().getMonthMenuId()); // month_menu_id 추가

        // 기본 질문과 추가 질문을 분리하여 처리
        List<SurveyDetailResponseDTO.QuestionSatisfactionDistribution> mandatoryQuestions = new ArrayList<>();
        List<SurveyDetailResponseDTO.QuestionSatisfactionDistribution> additionalQuestions = new ArrayList<>();

        for (Question question : survey.getQuestions()) {
            Long questionId = question.getId();
            String questionText = question.getQuestion();
            String answerType = question.getAnswerType();

            Map<Integer, Integer> satisfactionDistribution = null;
            List<String> textResponses = new ArrayList<>();

            if ("radio".equals(answerType) && questionText.contains("만족도 점수")) {
                satisfactionDistribution = createDistributionForMandatoryQuestions(survey.getResponses(), question);
            } else if ("text".equals(answerType)) {
                textResponses = createTextResponseForUser(survey.getResponses(), questionText);
            }

            SurveyDetailResponseDTO.QuestionSatisfactionDistribution questionDistribution =
                new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
                    questionId,
                    questionText,
                    satisfactionDistribution != null ? satisfactionDistribution : new HashMap<>(),
                    textResponses,
                    answerType
                );

            // 질문 유형에 따라 분리
            if (question.isMandatory()) {
                mandatoryQuestions.add(questionDistribution);
            } else {
                additionalQuestions.add(questionDistribution);
            }
        }

        response.setMandatoryQuestions(mandatoryQuestions);   // 기본 질문 리스트 설정
        response.setAdditionalQuestions(additionalQuestions); // 추가 질문 리스트 설정

        // 설문에 대한 평균 점수 계산
        SurveyDetailResponseDTO.AverageScores averageScores = calculateUserAverageScores(survey.getResponses());
        response.setAverageScores(averageScores);

        return response;
    }


    private Map<Integer, Integer> createDistributionForMandatoryQuestions(List<SurveyResponse> responses, Question question) {
        Map<Integer, Integer> distributionMap = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            distributionMap.put(i, 0);
        }

        responses.forEach(response -> {
            response.getResponseDetails().forEach(detail -> {
                if (detail.getQuestion().getId().equals(question.getId()) && question.isMandatory()) {
                    Integer score = detail.getAnswerScore();
                    if (score != null) {
                        distributionMap.put(score, distributionMap.getOrDefault(score, 0) + 1);
                    }
                }
            });
        });

        return distributionMap;
    }


    private List<String> createTextResponseForUser(List<SurveyResponse> responses, String questionText) {
        return responses.stream()
                        .flatMap(response -> response.getResponseDetails().stream()
                                                     .filter(detail -> detail.getQuestion().getQuestion().equals(questionText))
                                                     .flatMap(detail -> {
                                                         if (detail.getAnswerText() != null) {
                                                             return Stream.of(detail.getAnswerText());
                                                         } else if (detail.getAnswerList() != null) {
                                                             return detail.getAnswerList().stream();
                                                         }
                                                         return Stream.empty();
                                                     })
                        ).collect(Collectors.toList());
    }


    private SurveyDetailResponseDTO.AverageScores calculateUserAverageScores(List<SurveyResponse> responses) {
        double totalSatisfaction = responses.stream()
                                            .flatMap(response -> response.getResponseDetails().stream())
                                            .filter(detail -> detail.getQuestion().getQuestion().equals("월별 만족도 점수(1~10)"))
                                            .mapToDouble(detail -> Optional.ofNullable(detail.getAnswerScore()).orElse(0))
                                            .average().orElse(0.0);

        double portionSatisfaction = responses.stream()
                                              .flatMap(response -> response.getResponseDetails().stream())
                                              .filter(detail -> detail.getQuestion().getQuestion().equals("반찬 양 만족도 점수(1~10)"))
                                              .mapToDouble(detail -> Optional.ofNullable(detail.getAnswerScore()).orElse(0))
                                              .average().orElse(0.0);

        double hygieneSatisfaction = responses.stream()
                                              .flatMap(response -> response.getResponseDetails().stream())
                                              .filter(detail -> detail.getQuestion().getQuestion().equals("위생 만족도 점수(1~10)"))
                                              .mapToDouble(detail -> Optional.ofNullable(detail.getAnswerScore()).orElse(0))
                                              .average().orElse(0.0);

        double tasteSatisfaction = responses.stream()
                                            .flatMap(response -> response.getResponseDetails().stream())
                                            .filter(detail -> detail.getQuestion().getQuestion().equals("맛 만족도 점수(1~10)"))
                                            .mapToDouble(detail -> Optional.ofNullable(detail.getAnswerScore()).orElse(0))
                                            .average().orElse(0.0);

        SurveyDetailResponseDTO.AverageScores averageScores = new SurveyDetailResponseDTO.AverageScores();
        averageScores.setTotalSatisfaction(totalSatisfaction);
        averageScores.setPortionSatisfaction(portionSatisfaction);
        averageScores.setHygieneSatisfaction(hygieneSatisfaction);
        averageScores.setTasteSatisfaction(tasteSatisfaction);

        return averageScores;
    }


    @Transactional
    public SurveyResponseResponseDTO submitSurveyResponse(Long surveyId, SurveyResponseRequestDTO surveyResponseRequestDTO) {
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        SurveyResponse surveyResponse = new SurveyResponse(survey, LocalDateTime.now());

        // 기본 질문과 추가 질문의 응답을 각각 처리
        surveyResponseRequestDTO.getBasicQuestions().forEach(questionResponseDTO ->
            processQuestionResponse(survey, questionResponseDTO, surveyResponse)
        );

        surveyResponseRequestDTO.getAdditionalQuestions().forEach(questionResponseDTO ->
            processQuestionResponse(survey, questionResponseDTO, surveyResponse)
        );

        surveyResponseRepository.save(surveyResponse);
        return new SurveyResponseResponseDTO(surveyResponse.getId(), survey.getId());
    }

    private void processQuestionResponse(Survey survey, SurveyResponseRequestDTO.QuestionResponseDTO questionResponseDTO, SurveyResponse surveyResponse) {
        Question question = questionRepository.findById(questionResponseDTO.getQuestionId())
                                              .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Object answer = questionResponseDTO.getAnswer();
        String answerType = question.getAnswerType();

        // SurveyResponseDetail 객체 생성하여 응답을 저장
        SurveyResponseDetail responseDetail = new SurveyResponseDetail();
        responseDetail.setSurveyResponse(surveyResponse);
        responseDetail.setQuestion(question);

        // answerType에 따라 데이터를 다른 필드에 저장
        if ("radio".equals(answerType) && answer instanceof Integer) {
            responseDetail.setAnswerScore((Integer) answer);  // radio 타입의 점수 저장
        } else if ("text".equals(answerType)) {
            if (answer instanceof String) {
                responseDetail.setAnswerText((String) answer);  // 텍스트 응답 저장
            } else if (answer instanceof List ) {

                List<?> answerList = (List<?>) answer;

                if (answerList.isEmpty() || answerList.get(0) == null) {
                    // 설문내용이 없을 경우 예외를 던진다.
                    throw new CustomException(ErrorCode.SURVEY_CONTENT_REQUIRED);
                } else {
                    // 첫 번째 요소의 타입에 따라 변환
                    Object firstElement = answerList.get(0);

                    // LocalDateTime 타입의 경우 문자열로 변환하여 저장
                    if (firstElement instanceof LocalDateTime) {
                        List<String> converted = answerList.stream().map(obj -> ((LocalDateTime) obj).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .collect(Collectors.toList());
                        responseDetail.setAnswerList(converted);
                    }
                    // 그 외의 경우는 그대로 저장
                    else if (firstElement instanceof String) {
                        responseDetail.setAnswerList((List<String>) answer);  // 리스트 응답 저장
                    }
                }
            }
        } else {
            throw new CustomException(ErrorCode.INVALID_ANSWER_TYPE);
        }

        surveyResponseDetailRepository.save(responseDetail);

        // SurveyResponse에 응답 세부 정보 추가
        surveyResponse.addResponseDetail(responseDetail);
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

        // 추가 질문만 수정
        if (requestDTO.getQuestions() != null && !requestDTO.getQuestions().isEmpty()) {
            for (QuestionUpdateRequestDTO questionUpdateRequest : requestDTO.getQuestions()) {
                Question question = questionRepository.findByIdAndSurveyId(questionUpdateRequest.getQuestionId(), surveyId)
                                                      .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

                // 추가질문일 경우에만 수정
                if (!question.isMandatory()) {
                    question.reviseQuestion(questionUpdateRequest.getQuestion(), questionUpdateRequest.getAnswerType());
                    updatedQuestions.add(new SurveyUpdateResponseDTO.QuestionResponseDTO(question.getId(), LocalDateTime.now()));
                }

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
