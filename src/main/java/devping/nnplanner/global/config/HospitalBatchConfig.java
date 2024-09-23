package devping.nnplanner.global.config;

import devping.nnplanner.domain.openapi.batch.hospital.HospitalMenuItemProcessor;
import devping.nnplanner.domain.openapi.batch.hospital.HospitalMenuItemReader;
import devping.nnplanner.domain.openapi.batch.hospital.HospitalMenuItemWriter;
import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.entity.ImportHospitalMenu;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HospitalBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final HospitalMenuItemReader hospitalMenuItemReader;
    private final HospitalMenuItemProcessor hospitalMenuItemProcessor;
    private final HospitalMenuItemWriter hospitalMenuItemWriter;

    public HospitalBatchConfig(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               HospitalMenuItemReader hospitalMenuItemReader,
                               HospitalMenuItemProcessor hospitalMenuItemProcessor,
                               HospitalMenuItemWriter hospitalMenuItemWriter) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.hospitalMenuItemReader = hospitalMenuItemReader;
        this.hospitalMenuItemProcessor = hospitalMenuItemProcessor;
        this.hospitalMenuItemWriter = hospitalMenuItemWriter;
    }

    @Bean
    public Job importHospitalMenuJob() {
        return new JobBuilder("importHospitalMenuJob", jobRepository)
            .start(importHospitalMenuStep())
            .build();
    }

    @Bean
    public Step importHospitalMenuStep() {
        return new StepBuilder("importHospitalMenuStep", jobRepository)
            .<ImportHospitalMenu, HospitalMenu>chunk(1000, transactionManager)
            .reader(hospitalMenuItemReader)
            .processor(hospitalMenuItemProcessor)
            .writer(hospitalMenuItemWriter)
            .faultTolerant()
            .retryLimit(3)
            .retry(Exception.class)
            .allowStartIfComplete(true)
            .taskExecutor(hospitalTaskExecutor())
            .build();
    }

    @Bean
    public TaskExecutor hospitalTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
}