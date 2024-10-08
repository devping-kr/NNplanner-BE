//package devping.nnplanner.domain.survey.service;
//
//import devping.nnplanner.domain.survey.dto.request.SurveyRequestDTO;
//import devping.nnplanner.domain.survey.dto.response.SurveyResponseDTO;
//import devping.nnplanner.domain.survey.entity.Survey;
//import devping.nnplanner.domain.survey.repository.SurveyRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//class SurveyServiceTest {
//
//    @InjectMocks
//    private SurveyService surveyService;
//
//    @Mock
//    private SurveyRepository surveyRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this); // Mockito 초기화
//    }
//
//    @Test
//    void testCreateSurvey() {
//        // Given
//        SurveyRequestDTO requestDTO = new SurveyRequestDTO(
//            1L,
//            null, // 마감 기한 설정 없음 (2주 기본값 적용)
//            List.of("추가 질문 1", "추가 질문 2")
//        );
//
//        // Survey 생성 시 필수 질문 8개 + 추가 질문 2개 = 총 10개
//        List<String> combinedQuestions = new ArrayList<>(List.of(
//            "월별 만족도 점수(1~10)",
//            "반찬 양 만족도 점수(1~10)",
//            "위생 만족도 점수(1~10)",
//            "맛 만족도 점수(1~10)",
//            "가장 좋아하는 상위 3개 식단",
//            "가장 싫어하는 상위 3개 식단",
//            "먹고 싶은 메뉴",
//            "영양사에게 한마디",
//            "추가 질문 1",
//            "추가 질문 2"
//        ));
//        Survey expectedSurvey = new Survey(1L, LocalDateTime.now().plusWeeks(2), combinedQuestions);
//        when(surveyRepository.save(any(Survey.class))).thenReturn(expectedSurvey);
//
//        // When
//        SurveyResponseDTO createdSurvey = surveyService.createSurvey(requestDTO);
//
//        // Then
//        assertNotNull(createdSurvey);
//        assertEquals(1L, createdSurvey.getMmId());
//
//        // 총 질문 수가 10개 (필수 질문 8개 + 추가 질문 2개)인지 확인
//        assertEquals(10, createdSurvey.getQuestions().size()); // 질문 개수 확인
//
//        verify(surveyRepository, times(1)).save(any(Survey.class)); // save 메서드가 호출되었는지 확인
//    }
//
//}
