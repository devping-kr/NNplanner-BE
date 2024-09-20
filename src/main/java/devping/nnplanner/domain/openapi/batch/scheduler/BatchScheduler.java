package devping.nnplanner.domain.openapi.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;

    private final Job importFoodDataJob;

    @Scheduled(cron = "0 0 1 1/3 *")
    public void runJob() {
        try {
            jobLauncher.run(importFoodDataJob, new JobParameters());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
