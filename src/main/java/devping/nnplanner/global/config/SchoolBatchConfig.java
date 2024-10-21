package devping.nnplanner.global.config;

import devping.nnplanner.domain.openapi.batch.schoolMenu.SchoolMenuProcessor;
import devping.nnplanner.domain.openapi.batch.schoolMenu.SchoolMenuReader;
import devping.nnplanner.domain.openapi.batch.schoolMenu.SchoolMenuWriter;
import devping.nnplanner.domain.openapi.dto.response.SchoolMenuDataVO;
import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import devping.nnplanner.global.exception.CustomException;
import java.util.List;
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
public class SchoolBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SchoolMenuReader schoolMenuReader;
    private final SchoolMenuProcessor schoolMenuProcessor;
    private final SchoolMenuWriter schoolMenuWriter;

    public SchoolBatchConfig(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             SchoolMenuReader schoolMenuReader,
                             SchoolMenuProcessor schoolMenuProcessor,
                             SchoolMenuWriter schoolMenuWriter) {

        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.schoolMenuReader = schoolMenuReader;
        this.schoolMenuProcessor = schoolMenuProcessor;
        this.schoolMenuWriter = schoolMenuWriter;
    }

    @Bean
    public Job schoolMenuJob() {
        return new JobBuilder("schoolMenuJob", jobRepository)
            .start(schoolMenuStep())
            .build();
    }

    @Bean
    public Step schoolMenuStep() {
        return new StepBuilder("schoolMenuStep", jobRepository)
            .<SchoolMenuDataVO, List<SchoolMenu>>chunk(5, transactionManager)
            .reader(schoolMenuReader)
            .processor(schoolMenuProcessor)
            .writer(schoolMenuWriter)
            .faultTolerant()
            .skip(CustomException.class)
            .skipLimit(Integer.MAX_VALUE)
            .retryLimit(3)
            .retry(Exception.class)
            .allowStartIfComplete(true)
            .taskExecutor(schoolTaskExecutor())
            .build();
    }

    @Bean
    public TaskExecutor schoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
}
