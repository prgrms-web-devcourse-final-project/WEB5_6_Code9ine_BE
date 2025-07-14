package com.grepp.spring.infra.publicdata.batch.config;

import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.infra.publicdata.batch.apiclient.StoreApiClient;
import com.grepp.spring.infra.publicdata.batch.dto.StoreDto;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Set;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
@Profile("!mock")
public class StoreBatchConfig {


    private final StoreApiClient storeApiClient;
    private final StoreRepository storeRepository;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    //JobRepository 직접 구성하여 테이블 prefix 지정
    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager(entityManagerFactory));
        factory.setTablePrefix("batch_"); //소문자 테이블 사용하도록 설정
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    //트랜잭션 매니저도 수동 등록
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    //배치 Job 정의 / 시작점
    @Bean
    public Job storeJob(JobRepository jobRepository) {
        return new JobBuilder("storeJob", jobRepository)
                .start(storeStep(jobRepository, transactionManager(entityManagerFactory)))
                .build();
    }

    //배치 Step 정의 / 실행과정
    @Bean
    public Step storeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("storeStep", jobRepository)
                .<StoreDto, Store>chunk(100, transactionManager)
                .reader(storeItemReader())
                .processor(storeItemProcessor())
                .writer(storeItemWriter())
                .build();
    }

    // json 을 dto 로 리더 (내부에 만들어둔 조건 실행)
    @Bean
    public ItemReader<StoreDto> storeItemReader() {
        return new ListItemReader<>(storeApiClient.fetchFilteredStores());
    }

    // dto 를 entity 로 변환
    @Bean
    public ItemProcessor<StoreDto, Store> storeItemProcessor() {
        return dto -> {
            Store store = new Store();
            store.setSido(dto.get시도());
            store.setLocation(dto.get시군());
            store.setName(dto.get업소명());
            store.setCategory(extractCategory(dto.get업종()));

            store.setFirstMenu(dto.get메뉴1());
            store.setFirstPrice(parseInt(dto.get가격1()));
            store.setSecondMenu(dto.get메뉴2());
            store.setSecondPrice(parseInt(dto.get가격2()));
            store.setThirdMenu(dto.get메뉴3());
            store.setThirdPrice(parseInt(dto.get가격3()));

            store.setContact(dto.get연락처());
            store.setAddress(dto.get주소());

            store.setActivated(true);
            store.setCreatedAt(LocalDateTime.now());
            store.setModifiedAt(LocalDateTime.now());

            return store;
        };
    }

    // 저장 !
    @Bean
    public ItemWriter<Store> storeItemWriter() {
        return (ItemWriter<Store>) items -> {
            log.info("Writer 실행: {}개 저장 시도", items.size());

            if (!items.isEmpty()) {
                Store first = items.getItems().get(0);
                log.info("첫 번째 저장 대상: {}", first.getName());
            }

            storeRepository.saveAll(items);
            log.info("Writer 완료: {}개 저장됨", items.size());
        };
    }

    // 가격 인티져 또는 null 일 수 있으므로 안전 파싱
    private Integer parseInt(String value) {
        try {
            return (value != null && !value.isBlank()) ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }


    // 한식_ㅁㅁ 한식으로 저장
    private static final Set<String> allowedCategories = Set.of(
            "한식", "중식", "일식", "양식", "미용업", "세탁업", "숙박업"
    );

    private String extractCategory(String rawCategory) {
        if (rawCategory == null) return null;
        return allowedCategories.stream()
                .filter(rawCategory::contains)
                .findFirst()
                .orElse(rawCategory); // 없으면 원래 문자열 유지
    }
}
