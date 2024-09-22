package devping.nnplanner.domain.openapi.service;

import devping.nnplanner.domain.openapi.dto.response.SchoolInfoResponseDTO;
import devping.nnplanner.domain.openapi.dto.response.SchoolInfoResponseDTO.SchoolRow;
import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SchoolInfoService {

    private final SchoolInfoRepository schoolInfoRepository;
    private final WebClient webClient;

    private Integer pageNo = 1;
    private final Integer numOfRows = 100;
    private int totalPages = Integer.MAX_VALUE;

    @Value("${api.school.key}")
    private String schoolApiKey;

    @Autowired
    public SchoolInfoService(
        WebClient schoolWebClient,
        SchoolInfoRepository schoolInfoRepository) {

        this.webClient = schoolWebClient;
        this.schoolInfoRepository = schoolInfoRepository;
    }

    public void getAllSchoolInfo() {

        while (pageNo <= totalPages) {

            SchoolInfoResponseDTO response =
                webClient.get()
                         .uri(uriBuilder -> uriBuilder
                             .path("/schoolInfo")
                             .queryParam("KEY", schoolApiKey)
                             .queryParam("Type", "json")
                             .queryParam("pIndex", pageNo)
                             .queryParam("pSize", numOfRows)
                             .build())
                         .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                         .retrieve()
                         .bodyToMono(SchoolInfoResponseDTO.class)
                         .block();

            if (response != null && response.getSchoolData() != null) {

                List<SchoolRow> schoolRows =
                    response.getSchoolData().stream()
                            .flatMap(schoolData -> schoolData.getRow().stream())
                            .collect(Collectors.toList());

                saveSchoolInfo(schoolRows);

                pageNo++;

            } else {
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
