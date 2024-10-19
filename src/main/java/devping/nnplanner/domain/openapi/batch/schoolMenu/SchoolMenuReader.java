package devping.nnplanner.domain.openapi.batch.schoolMenu;

import com.fasterxml.jackson.databind.ObjectMapper;
import devping.nnplanner.domain.openapi.dto.response.SchoolMenuApiResponseDTO;
import devping.nnplanner.domain.openapi.dto.response.SchoolMenuDataVO;
import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Component
public class SchoolMenuReader implements ItemReader<SchoolMenuDataVO> {

    private final WebClient webClient;
    private Integer pageNo = 1;
    private final Integer numOfRows = 100;
    private Long currentId = 1L;
    private Lock lock;

    private final SchoolInfoRepository schoolInfoRepository;

    @Value("${api.school.key}")
    private String schoolApiKey;

    @Autowired
    public SchoolMenuReader(WebClient schoolWebClient, SchoolInfoRepository schoolInfoRepository) {
        this.webClient = schoolWebClient;
        this.schoolInfoRepository = schoolInfoRepository;
        this.lock = new ReentrantLock();
    }

    @Override
    public SchoolMenuDataVO read() throws Exception {

        // 락을 시도, 락을 얻지 못하면 건너뜀
        if (!lock.tryLock(100, TimeUnit.MILLISECONDS)) {
            log.warn("다른 스레드에서 처리 중입니다. 현재 ID: {}", currentId);
            return null; // 락을 얻지 못했을 때 null 반환
        }

        try {
            // SchoolInfo 가져오기
            SchoolInfo schoolInfo =
                schoolInfoRepository.findById(currentId)
                                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            log.info("현재 ID: {}, 데이터를 읽었습니다.", currentId);
            currentId++; // 다음 ID로 증가

            // API 호출
            SchoolMenuApiResponseDTO response =
                webClient.get()
                         .uri(uriBuilder -> uriBuilder
                             .path("/mealServiceDietInfo")
                             .queryParam("KEY", schoolApiKey)
                             .queryParam("Type", "json")
                             .queryParam("pIndex", pageNo)
                             .queryParam("pSize", numOfRows)
                             .queryParam("ATPT_OFCDC_SC_CODE", schoolInfo.getSchoolAreaCode())
                             .queryParam("SD_SCHUL_CODE", schoolInfo.getSchoolCode())
                             .build())
                         .retrieve()
                         .bodyToMono(SchoolMenuApiResponseDTO.class)
                         .block();

            // 응답 데이터 null 체크 및 처리
            if (response == null || response.getMealServiceDietInfo() == null ||
                response.getMealServiceDietInfo().isEmpty()) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            // 식단 이름 파싱 및 리스트 변환
            List<String[]> foodNamesList = response.getMealServiceDietInfo().get(1).getRow()
                                                   .stream()
                                                   .map(row -> row.getSchoolMenuName()
                                                                  .split("<br/>"))
                                                   .filter(arr -> arr.length > 3)
                                                   .collect(Collectors.toList());

            log.info("Reader 단계에서 복사된 값: {}",
                new ObjectMapper().writeValueAsString(foodNamesList));

            return new SchoolMenuDataVO(schoolInfo.getSchoolCode(), foodNamesList);

        } finally {
            lock.unlock(); // 락 해제
        }
    }
}