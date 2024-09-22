package devping.nnplanner.global.config;

import devping.nnplanner.domain.openapi.batch.food.FoodItemProcessor;
import devping.nnplanner.domain.openapi.batch.food.FoodItemReader;
import devping.nnplanner.domain.openapi.batch.food.FoodItemWriter;
import devping.nnplanner.domain.openapi.dto.response.FoodApiResponseDTO.FoodItem;
import devping.nnplanner.domain.openapi.entity.Food;
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
public class FoodBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FoodItemReader foodItemReader;
    private final FoodItemProcessor foodItemProcessor;
    private final FoodItemWriter foodItemWriter;

    public FoodBatchConfig(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           FoodItemReader foodItemReader,
                           FoodItemProcessor foodItemProcessor,
                           FoodItemWriter foodItemWriter) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.foodItemReader = foodItemReader;
        this.foodItemProcessor = foodItemProcessor;
        this.foodItemWriter = foodItemWriter;
    }

    @Bean
    public Job importFoodDataJob() {
        return new JobBuilder("importFoodDataJob", jobRepository)
            .start(step())
            .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
            .<List<FoodItem>, List<Food>>chunk(100, transactionManager)
            .reader(foodItemReader)
            .processor(foodItemProcessor)
            .writer(foodItemWriter)
            .faultTolerant()
            .retryLimit(3)
            .retry(Exception.class)
            .allowStartIfComplete(true)
            .taskExecutor(foodTaskExecutor())
            .build();
    }

    @Bean
    public TaskExecutor foodTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
}