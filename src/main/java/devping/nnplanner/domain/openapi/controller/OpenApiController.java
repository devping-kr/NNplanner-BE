package devping.nnplanner.domain.openapi.controller;

import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
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

    @GetMapping("/food")
    public ResponseEntity<ApiResponse<Void>> startBatch() {
        try {
            jobLauncher.run(importFoodDataJob, new JobParameters());

            return GlobalResponse.OK("음식 api 호출 성공", null);

        } catch (Exception e) {
            log.error(e.getMessage());

            return GlobalResponse.BAD_REQUEST("음식 api 호출 실패", null);
        }
    }
}