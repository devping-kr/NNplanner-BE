package devping.nnplanner.domain.openapi.controller;

import devping.nnplanner.domain.openapi.service.BatchService;
import devping.nnplanner.domain.openapi.service.SchoolInfoService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/open-apis")
@RestController
@RequiredArgsConstructor
public class OpenApiController {

    private final JobLauncher jobLauncher;
    private final Job importHospitalMenuJob;
    private final SchoolInfoService schoolInfoService;
    private final BatchService batchService;

    @GetMapping("/food")
    public ResponseEntity<ApiResponse<Void>> startFoodBatch() {

        batchService.startFoodBatchJob();

        return GlobalResponse.OK("음식 API 호출 성공, 배치 작업이 백그라운드에서 실행됩니다.", null);
    }


    @GetMapping("/school-info")
    public ResponseEntity<ApiResponse<Void>> getSchoolInfo() {

        schoolInfoService.getAllSchoolInfo();

        return GlobalResponse.OK("학교 정보 api 호출 성공, 작업이 백그라운드에서 실행됩니다.", null);
    }

    @GetMapping("/hospital")
    public ResponseEntity<ApiResponse<Void>> getHospitalMenu() {

        batchService.startHospitalMenuBatchJob();

        return GlobalResponse.OK("병원 메뉴 필터 성공, 배치 작업이 백그라운드에서 실행됩니다.", null);
    }
}