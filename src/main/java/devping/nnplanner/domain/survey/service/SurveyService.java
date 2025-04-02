package devping.nnplanner.domain.survey.service;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.survey.dto.request.QuestionUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyResponseRequestDTO;
import devping.nnplanner.domain.survey.dto.request.SurveyUpdateRequestDTO;
import devping.nnplanner.domain.survey.dto.response.*;
import devping.nnplanner.domain.survey.entity.*;
import devping.nnplanner.domain.survey.repository.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SurveyService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyService.class);

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final MonthMenuRepository monthMenuRepository;
    private final QuestionRepository questionRepository;
    private final ResponseDetailRepository surveyResponseDetailRepository;
    private final SurveyAnswerItemRepository surveyAnswerItemRepository;


    public SurveyService(SurveyRepository surveyRepository, SurveyResponseRepository surveyResponseRepository, MonthMenuRepository monthMenuRepository, QuestionRepository questionRepository, ResponseDetailRepository surveyResponseDetailRepository, SurveyAnswerItemRepository surveyAnswerItemRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.monthMenuRepository = monthMenuRepository;
        this.questionRepository = questionRepository;
        this.surveyResponseDetailRepository = surveyResponseDetailRepository;
        this.surveyAnswerItemRepository = surveyAnswerItemRepository;
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



    @Transactional
    public SurveyDetailResponseDTO getSurveyDetail(UserDetailsImpl userDetails, Long surveyId) {
        Long userId = userDetails.getUser().getUserId();

        Survey survey = surveyRepository.findByIdAndUser_UserId(surveyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        List<Question> questions = questionRepository.findAllBySurveyId(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        List<SurveyResponse> surveyResponses = surveyResponseRepository.findBySurveyId(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        List<SurveyResponseDetail> details = null;

        List<SurveyDetailResponseDTO.QuestionSatisfactionDistribution> mandatoryQuestions = new ArrayList<>();
        List<SurveyDetailResponseDTO.QuestionSatisfactionDistribution> additionalQuestions = new ArrayList<>();

        for (Question question : questions) {
            Long questionId = question.getId();
            String questionText = question.getQuestion();
            String answerType = question.getAnswerType();

            Map<Integer, Integer> satisfactionDistribution = new HashMap<>();
            List<String> textResponses = new ArrayList<>();

            for (SurveyResponse response : surveyResponses) {
                details = surveyResponseDetailRepository.findSurveyResponseDetailBySurveyResponse(response.getId())
                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESPONSE_NOT_FOUND));

                for (SurveyResponseDetail detail : details) {
                    if (!detail.getQuestion().getId().equals(questionId)) continue;

                    List<SurveyAnswerItem> answerItems = surveyAnswerItemRepository.findSurveyAnswerItemByResponseDetailId(detail.getId());
                    for (SurveyAnswerItem item : answerItems) {
                        if (item.getAnswerItemType() == AnswerItemType.RADIO && item.getAnswerScore() != null) {
                            satisfactionDistribution.merge(item.getAnswerScore(), 1, Integer::sum);
                        } else if (item.getAnswerItemType().equals(AnswerItemType.DATE)) {
                            textResponses.add(item.getAnswerItem());
                        }
                    }
                }
            }

            // 텍스트 응답 중 날짜 형식 값만 추출하여 상위 3개만 남김
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<String> validDates = textResponses.stream()
                    .filter(s -> {
                        try {
                            LocalDate.parse(s, formatter);
                            return true;
                        } catch (DateTimeParseException e) {
                            return false;
                        }
                    })
                    .toList();

            if (!validDates.isEmpty()) {
                Map<String, Long> frequency = validDates.stream()
                        .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
                textResponses = frequency.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .limit(3)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            }

            SurveyDetailResponseDTO.QuestionSatisfactionDistribution dto =
                    new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
                            questionId,
                            questionText,
                            satisfactionDistribution,
                            textResponses,
                            answerType
                    );

            if (question.isMandatory()) {
                mandatoryQuestions.add(dto);
            } else {
                additionalQuestions.add(dto);
            }
        }

        SurveyDetailResponseDTO response = SurveyDetailResponseDTO.builder()
                .surveyName(survey.getSurveyName())
                .deadline(survey.getDeadlineAt())
                .mmId(survey.getMonthMenu().getMonthMenuId())
                .mandatoryQuestions(mandatoryQuestions)
                .additionalQuestions(additionalQuestions)
                .build();


        // 1. surveyResponses 루프를 돌며 answerItemType이 RADIO인 것만 수집
        List<SurveyAnswerItem> allRadioItems = new ArrayList<>();

        for (SurveyResponse surveyResponse : surveyResponses) {
            List<SurveyResponseDetail> surveyResponseDetails = surveyResponseDetailRepository.findSurveyResponseDetailBySurveyResponse(surveyResponse.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESPONSE_NOT_FOUND));

            for (SurveyResponseDetail detail : surveyResponseDetails) {
                List<SurveyAnswerItem> items = surveyAnswerItemRepository.findSurveyAnswerItemByResponseDetailId(detail.getId());
                for (SurveyAnswerItem item : items) {
                    if (item.getAnswerItemType() == AnswerItemType.RADIO && item.getAnswerScore() != null) {
                        allRadioItems.add(item);
                    }
                }
            }
        }

// 2. 평균 점수 계산 함수 수정
        SurveyDetailResponseDTO.AverageScores averageScores = calculateUserAverageScores(allRadioItems);
        response.setAverageScores(averageScores);



        return response;
    }





    private SurveyDetailResponseDTO.AverageScores calculateUserAverageScores(List<SurveyAnswerItem> items) {
        Function<String, Double> scoreForQuestion = (questionText) ->
                items.stream()
                        .filter(i -> i.getSurveyResponseDetail().getQuestion().getQuestion().equals(questionText))
                        .mapToDouble(i -> Optional.ofNullable(i.getAnswerScore()).orElse(0))
                        .average()
                        .orElse(0.0);

        SurveyDetailResponseDTO.AverageScores averageScores = new SurveyDetailResponseDTO.AverageScores();
        averageScores.setTotalSatisfaction(scoreForQuestion.apply("월별 만족도 점수(1~10)"));
        averageScores.setPortionSatisfaction(scoreForQuestion.apply("반찬 양 만족도 점수(1~10)"));
        averageScores.setHygieneSatisfaction(scoreForQuestion.apply("위생 만족도 점수(1~10)"));
        averageScores.setTasteSatisfaction(scoreForQuestion.apply("맛 만족도 점수(1~10)"));
        return averageScores;
    }






    @Transactional
    public SurveyResponseResponseDTO submitSurveyResponse(Long surveyId, SurveyResponseRequestDTO surveyResponseRequestDTO) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        SurveyResponse surveyResponse = new SurveyResponse(survey, LocalDateTime.now());
        surveyResponseRepository.save(surveyResponse);

        // 기본 질문과 추가 질문의 응답을 각각 처리
        surveyResponseRequestDTO.getBasicQuestions().forEach(questionResponseDTO ->
                processQuestionResponse(survey, questionResponseDTO, surveyResponse)
        );

        surveyResponseRequestDTO.getAdditionalQuestions().forEach(questionResponseDTO ->
                processQuestionResponse(survey, questionResponseDTO, surveyResponse)
        );


        return new SurveyResponseResponseDTO(surveyResponse.getId(), survey.getId());
    }

    private void processQuestionResponse(Survey survey, SurveyResponseRequestDTO.QuestionResponseDTO questionResponseDTO, SurveyResponse surveyResponse) {
        Question question = questionRepository.findById(questionResponseDTO.getQuestionId())
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));


        // SurveyResponseDetail 객체 생성하여 응답을 저장
        SurveyResponseDetail surveyResponseDetail = SurveyResponseDetail.builder()
                .surveyResponse(surveyResponse)
                .question(question)
                .build();

        surveyResponseDetailRepository.save(surveyResponseDetail);

        surveyResponse.addResponseDetail(surveyResponseDetail);


        Object answer = questionResponseDTO.getAnswer();
        String answerType = question.getAnswerType();

        // answerType에 따라 데이터를 다른 필드에 저장


        if ("radio".equals(answerType) && answer instanceof Integer) {
            SurveyAnswerItem surveyAnswerItem = SurveyAnswerItem.builder()
                    .surveyResponseDetail(surveyResponseDetail)
                    .answerScore((Integer) answer)
                    .answerItemType(AnswerItemType.RADIO)
                    .build();

            surveyAnswerItemRepository.save(surveyAnswerItem);


        } else if ("text".equals(answerType)) {
            if (answer instanceof String) {
                SurveyAnswerItem surveyAnswerItem = SurveyAnswerItem.builder()
                        .surveyResponseDetail(surveyResponseDetail)
                        .answerItem((String) answer)
                        .answerItemType(AnswerItemType.TEXT)
                        .build();

                surveyAnswerItemRepository.save(surveyAnswerItem);

            } else if (answer instanceof List) {

                List<?> answerList = (List<?>) answer;

                if (answerList.isEmpty() || answerList.get(0) == null) {
                    // 설문내용이 없을 경우 예외를 던진다.
                    throw new CustomException(ErrorCode.SURVEY_CONTENT_REQUIRED);
                } else {

                    for (Object obj : answerList) {
                        String value = String.valueOf(obj);
                        AnswerItemType answerItemType = isDateString(value) ? AnswerItemType.DATE : AnswerItemType.TEXT;
                        surveyAnswerItemRepository.save(
                                SurveyAnswerItem.builder()
                                        .surveyResponseDetail(surveyResponseDetail)
                                        .answerItem(value)
                                        .answerItemType(answerItemType)
                                        .build()
                        );


                    }

                }
            }
        } else {
            throw new CustomException(ErrorCode.INVALID_ANSWER_TYPE);
        }


    }

    private boolean isDateString(String input) {
        try {
            LocalDate.parse(input);  // 기본 포맷 yyyy-MM-dd
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
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
