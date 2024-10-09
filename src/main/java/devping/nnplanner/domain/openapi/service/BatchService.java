package devping.nnplanner.domain.openapi.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchService {

    private final JobLauncher jobLauncher;
    private final Job importFoodDataJob;
    private final Job importHospitalMenuJob;

    @Async
    public void startFoodBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();

            jobLauncher.run(importFoodDataJob, jobParameters);
            log.info("음식 배치 작업 시작");

        } catch (Exception e) {
            log.error("음식 배치 작업 실패: {}", e.getMessage());
        }
    }

    @Async
    public void startHospitalMenuBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();

            jobLauncher.run(importHospitalMenuJob, jobParameters);
            log.info("병원 메뉴 배치 작업 시작");

        } catch (Exception e) {
            log.error("배치 작업 실패: {}", e.getMessage());
        }
    }
}