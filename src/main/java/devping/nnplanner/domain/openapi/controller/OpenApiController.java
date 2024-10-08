package devping.nnplanner.domain.openapi.controller;

import devping.nnplanner.domain.openapi.service.SchoolInfoService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/openapis")
@RestController
@RequiredArgsConstructor
public class OpenApiController {

    private final JobLauncher jobLauncher;
    private final Job importFoodDataJob;
    private final Job importHospitalMenuJob;
    private final SchoolInfoService schoolInfoService;

    @GetMapping("/food")
    public ResponseEntity<ApiResponse<Void>> startFoodBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();

            jobLauncher.run(importFoodDataJob, jobParameters);

            return GlobalResponse.OK("음식 api 호출 성공", null);

        } catch (Exception e) {

            log.error(e.getMessage());

            return GlobalResponse.INTERNAL_SERVER_ERROR("음식 api 호출 실패", null);
        }
    }

    @GetMapping("/schoolinfo")
    public ResponseEntity<ApiResponse<Void>> getSchoolInfo() {

        schoolInfoService.getAllSchoolInfo();

        return GlobalResponse.OK("학교 정보 api 호출 성공", null);
    }

    @GetMapping("/hospital")
    public ResponseEntity<ApiResponse<Void>> getHospitalMenu() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();

            jobLauncher.run(importHospitalMenuJob, jobParameters);

            return GlobalResponse.OK("병원 메뉴 필터 성공", null);

        } catch (JobExecutionException e) {

            log.error(e.getMessage());

            return GlobalResponse.INTERNAL_SERVER_ERROR("병원 메뉴 필터 실행 실패", null);
        }
    }
}