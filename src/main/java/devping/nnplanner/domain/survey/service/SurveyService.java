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
            Long questionId = question.getId();
            String questionText = question.getQuestion();
            String answerType = question.getAnswerType();

            Map<Integer, Integer> satisfactionDistribution = null;
            List<String> textResponses = new ArrayList<>();

            if ("radio".equals(answerType) && questionText.contains("만족도 점수")) {
                // 만족도 질문 분포 생성
                satisfactionDistribution = switch (questionText) {
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
            } else if ("text".equals(answerType)) {
                // 텍스트 응답 처리
                switch (questionText) {
                    case "가장 좋아하는 상위 3개 식단" -> {
                        textResponses = responses.stream()
                                                 .flatMap(r -> r.getLikedMenus().stream())
                                                 .filter(menu -> !menu.trim().isEmpty())
                                                 .distinct()
                                                 .collect(Collectors.toList());
                    }
                    case "가장 싫어하는 상위 3개 식단" -> {
                        textResponses = responses.stream()
                                                 .flatMap(r -> r.getDislikedMenus().stream())
                                                 .filter(menu -> !menu.trim().isEmpty())
                                                 .distinct()
                                                 .collect(Collectors.toList());
                    }
                    case "먹고 싶은 메뉴" -> {
                        textResponses = responses.stream()
                                                 .flatMap(r -> r.getDesiredMenus().stream())
                                                 .distinct()
                                                 .collect(Collectors.toList());
                    }
                    case "영양사에게 한마디" -> {
                        textResponses = responses.stream()
                                                 .map(SurveyResponse::getMessagesToDietitian)
                                                 .filter(Objects::nonNull)
                                                 .distinct()
                                                 .collect(Collectors.toList());
                    }
                }
            }

            // 질문별로 분포를 ResponseDTO로 추가
            SurveyDetailResponseDTO.QuestionSatisfactionDistribution questionDistribution =
                new SurveyDetailResponseDTO.QuestionSatisfactionDistribution(
                    questionId,
                    questionText,
                    satisfactionDistribution != null ? satisfactionDistribution : new HashMap<>(),
                    textResponses,
                    answerType
                );

            satisfactionDistributions.add(questionDistribution);
        }

        response.setSatisfactionDistributions(satisfactionDistributions);

        List<Object[]> avgScoresResult = surveyResponseRepository.findAverageScores(surveyId);
        SurveyDetailResponseDTO.AverageScores averageScores = new SurveyDetailResponseDTO.AverageScores();

        if (avgScoresResult != null && !avgScoresResult.isEmpty()) {
            Object[] avgScores = avgScoresResult.get(0); // 첫 번째 결과 행을 가져옴
            averageScores.setTotalSatisfaction(avgScores[0] != null ? ((Double) avgScores[0]) : 0.0);
            averageScores.setPortionSatisfaction(avgScores[1] != null ? ((Double) avgScores[1]) : 0.0);
            averageScores.setHygieneSatisfaction(avgScores[2] != null ? ((Double) avgScores[2]) : 0.0);
            averageScores.setTasteSatisfaction(avgScores[3] != null ? ((Double) avgScores[3]) : 0.0);
        } else {
            // 평균 점수 계산 오류를 디버깅하기 위한 출력
            System.out.println("Average scores not calculated correctly. List: " + avgScoresResult);
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

        for (SurveyResponseRequestDTO.ResponseDTO response : surveyResponseRequestDTO.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId())
                                                  .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.setSurvey(survey);
            surveyResponse.setQuestion(question);
            surveyResponse.setResponseDate(LocalDateTime.now());

            if ("radio".equalsIgnoreCase(question.getAnswerType()) && response.getSatisfactionScore() != null) {
                surveyResponse.setSatisfactionScore(response.getSatisfactionScore());
            } else if ("text".equalsIgnoreCase(question.getAnswerType()) && response.getTextAnswer() != null) {
                surveyResponse.setMessagesToDietitian(response.getTextAnswer());
            }

            if (response.getLikedMenusTop3() != null) {
                // 메뉴 리스트를 콤마로 조인하지 않고 바로 리스트로 설정
                List<String> likedMenus = response.getLikedMenusTop3().stream()
                                                  .flatMap(menuSelectionResponseDTO -> menuSelectionResponseDTO.getMenus().stream())
                                                  .collect(Collectors.toList());
                surveyResponse.setLikedMenus(likedMenus);
            }

            if (response.getDislikedMenusTop3() != null) {
                // 메뉴 리스트를 콤마로 조인하지 않고 바로 리스트로 설정
                List<String> dislikedMenus = response.getDislikedMenusTop3().stream()
                                                     .flatMap(menuSelectionResponseDTO -> menuSelectionResponseDTO.getMenus().stream())
                                                     .collect(Collectors.toList());
                surveyResponse.setDislikedMenus(dislikedMenus);
            }

            if (response.getDesiredMenus() != null) {
                surveyResponse.setDesiredMenus(response.getDesiredMenus());
            }

            if (response.getMessageToDietitian() != null) {
                surveyResponse.setMessagesToDietitian(response.getMessageToDietitian());
            }

            surveyResponseRepository.save(surveyResponse);
        }

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

        // 질문 수정
        if (requestDTO.getQuestions() != null && !requestDTO.getQuestions().isEmpty()) {
            for (QuestionUpdateRequestDTO questionUpdateRequest : requestDTO.getQuestions()) {
                Question question = questionRepository.findByIdAndSurveyId(questionUpdateRequest.getQuestionId(), surveyId)
                                                      .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

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
