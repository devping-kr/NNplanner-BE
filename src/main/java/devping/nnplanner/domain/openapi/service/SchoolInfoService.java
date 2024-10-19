package devping.nnplanner.domain.openapi.service;

import devping.nnplanner.domain.openapi.dto.response.SchoolInfoResponseDTO;
import devping.nnplanner.domain.openapi.dto.response.SchoolInfoResponseDTO.SchoolRow;
import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class SchoolInfoService {

    private final SchoolInfoRepository schoolInfoRepository;
    private final WebClient webClient;

    private Integer pageNo = 1;
    private final Integer numOfRows = 100;

    @Value("${api.school.key}")
    private String schoolApiKey;

    public SchoolInfoService(
        WebClient schoolWebClient,
        SchoolInfoRepository schoolInfoRepository) {

        this.webClient = schoolWebClient;
        this.schoolInfoRepository = schoolInfoRepository;
    }

    @Async
    @Transactional
    public void getAllSchoolInfo() {

        log.info("start");

        while (true) {

            SchoolInfoResponseDTO response =
                webClient.get()
                         .uri(uriBuilder -> uriBuilder
                             .path("/schoolInfo")
                             .queryParam("KEY", schoolApiKey)
                             .queryParam("Type", "json")
                             .queryParam("pIndex", pageNo)
                             .queryParam("pSize", numOfRows)
                             .build())
                         .retrieve()
                         .bodyToMono(SchoolInfoResponseDTO.class)
                         .block();

            if (response != null &&
                response.getSchoolInfo() != null &&
                !response.getSchoolInfo().isEmpty()) {

                saveSchoolInfo(response.getSchoolInfo().get(1).getRow());
                pageNo++;

            } else {
                pageNo = 1;
                log.info("end");
                break;
            }
        }
    }

    private void saveSchoolInfo(List<SchoolRow> schoolRows) {

        List<SchoolInfo> schoolInfos =

            schoolRows.stream()
                      .filter(
                          schoolRow ->
                              !schoolInfoRepository.existsBySchoolCode(schoolRow.getSchoolCode()))
                      .map(this::convertToEntity)
                      .toList();

        schoolInfoRepository.saveAll(schoolInfos);
    }

    private SchoolInfo convertToEntity(SchoolRow schoolRow) {

        SchoolInfo schoolInfo = new SchoolInfo();

        schoolInfo.create(
            schoolRow.getSchoolAreaCode(),
            schoolRow.getSchoolAreaName(),
            schoolRow.getSchoolCode(),
            schoolRow.getSchoolName(),
            schoolRow.getSchoolKindName());

        return schoolInfo;
    }
}
