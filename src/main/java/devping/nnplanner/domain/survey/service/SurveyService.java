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

    public SurveyService(SurveyRepository surveyRepository,
                         MonthMenuRepository monthMenuRepository,
                         SurveyResponseRepository surveyResponseRepository,
                         QuestionRepository questionRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.monthMenuRepository = monthMenuRepository;
        this.questionRepository = questionRepository;
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
    public SurveyDetailResponseDTO getSurveyDetail(UserDetailsImpl userDetails, Long surveyId) {
        // 설문 조회
        Survey survey = surveyRepository.findById(surveyId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        // 설문 생성한 사용자와 로그인한 사용자가 동일한지 확인
        if (!survey.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 설문 상세 응답 DTO 구성
        SurveyDetailResponseDTO response = new SurveyDetailResponseDTO();
        response.setSurveyName(survey.getSurveyName());
        response.setDeadline(survey.getDeadlineAt());

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
                satisfactionDistribution = createDistributionForUser(survey.getResponses(), questionText);
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


    private Map<Integer, Integer> createDistributionForUser(List<SurveyResponse> responses, String questionText) {
        // 현재 사용자에 대한 만족도 분포를 생성하는 로직
        Map<Integer, Integer> distributionMap = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            distributionMap.put(i, 0);
        }

        // 각 응답의 만족도 점수를 이용해 분포 생성
        responses.forEach(response -> {
            if ("월별 만족도 점수(1~10)".equals(questionText) && response.getMonthlySatisfaction() != -1) {
                Integer score = response.getMonthlySatisfaction();
                distributionMap.put(score, distributionMap.getOrDefault(score, 0) + 1);
            } else if ("반찬 양 만족도 점수(1~10)".equals(questionText) && response.getPortionSatisfaction() != -1) {
                Integer score = response.getPortionSatisfaction();
                distributionMap.put(score, distributionMap.getOrDefault(score, 0) + 1);
            } else if ("위생 만족도 점수(1~10)".equals(questionText) && response.getHygieneSatisfaction() != -1) {
                Integer score = response.getHygieneSatisfaction();
                distributionMap.put(score, distributionMap.getOrDefault(score, 0) + 1);
            } else if ("맛 만족도 점수(1~10)".equals(questionText) && response.getTasteSatisfaction() != -1) {
                Integer score = response.getTasteSatisfaction();
                distributionMap.put(score, distributionMap.getOrDefault(score, 0) + 1);
            }
        });

        return distributionMap;
    }


    private List<String> createTextResponseForUser(List<SurveyResponse> responses, String questionText) {
        return responses.stream()
                        .flatMap(response -> {
                            if ("가장 좋아하는 상위 3개 식단".equals(questionText)) {
                                // likedMenus 리스트 반환
                                return response.getLikedMenus() != null ? response.getLikedMenus().stream() : Stream.empty();
                            } else if ("가장 싫어하는 상위 3개 식단".equals(questionText)) {
                                // dislikedMenus 리스트 반환
                                return response.getDislikedMenus() != null ? response.getDislikedMenus().stream() : Stream.empty();
                            } else if ("먹고 싶은 메뉴".equals(questionText)) {
                                // desiredMenus 리스트 반환
                                return response.getDesiredMenus() != null ? response.getDesiredMenus().stream() : Stream.empty();
                            } else if ("영양사에게 한마디".equals(questionText)) {
                                // messagesToDietitian 문자열 반환 (리스트로 감싸서 반환)
                                return response.getMessagesToDietitian() != null ? Stream.of(response.getMessagesToDietitian()) : Stream.empty();
                            }
                            return Stream.empty();
                        })
//                        .distinct() // 중복된 응답을 제거
                        .collect(Collectors.toList()); // 결과를 리스트로 수집
    }


    private SurveyDetailResponseDTO.AverageScores calculateUserAverageScores(List<SurveyResponse> responses) {
        double totalSatisfaction = responses.stream()
                                            .mapToDouble(SurveyResponse::getMonthlySatisfaction)
                                            .average().orElse(0.0);

        double portionSatisfaction = responses.stream()
                                              .mapToDouble(SurveyResponse::getPortionSatisfaction)
                                              .average().orElse(0.0);

        double hygieneSatisfaction = responses.stream()
                                              .mapToDouble(SurveyResponse::getHygieneSatisfaction)
                                              .average().orElse(0.0);

        double tasteSatisfaction = responses.stream()
                                            .mapToDouble(SurveyResponse::getTasteSatisfaction)
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

        // 기본 질문 처리
        if (surveyResponseRequestDTO.getBasicQuestions() != null) {
            processQuestionResponses(survey, surveyResponseRequestDTO.getBasicQuestions());
        }

        // 추가 질문 처리
        if (surveyResponseRequestDTO.getAdditionalQuestions() != null) {
            processQuestionResponses(survey, surveyResponseRequestDTO.getAdditionalQuestions());
        }

        SurveyResponse lastResponse = surveyResponseRepository.findTopBySurveyOrderByResponseDateDesc(survey);
        if (lastResponse == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return new SurveyResponseResponseDTO(lastResponse.getId(), survey.getId());
    }

    private void processQuestionResponses(Survey survey, List<SurveyResponseRequestDTO.QuestionResponseDTO> questionResponses) {
        for (SurveyResponseRequestDTO.QuestionResponseDTO questionResponse : questionResponses) {
            Question question = questionRepository.findById(questionResponse.getQuestionId())
                                                  .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.setSurvey(survey);
            surveyResponse.setQuestion(question);
            surveyResponse.setResponseDate(LocalDateTime.now());

            // answer 처리
            Object answer = questionResponse.getAnswer();
            if (answer instanceof Integer) {
                surveyResponse.setSatisfactionScore((Integer) answer); // 점수는 숫자로 처리
            } else if (answer instanceof String) {
                surveyResponse.setMessagesToDietitian((String) answer); // 메시지는 문자열로 처리
            } else if (answer instanceof List) {
                surveyResponse.setDesiredMenus((List<String>) answer); // 메뉴 리스트 처리
            } else {
                throw new CustomException(ErrorCode.INVALID_ANSWER_TYPE);
            }

            surveyResponseRepository.save(surveyResponse);
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

                // 필수 질문인지 확인하고, 필수 질문이면 수정 불가
                if (question.isMandatory()) {
                    throw new CustomException(ErrorCode.CANNOT_MODIFY_MANDATORY_QUESTION);
                }

                // 추가 질문만 수정
                question.setQuestion(questionUpdateRequest.getQuestion());
                question.setAnswerType(questionUpdateRequest.getAnswerType());
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
