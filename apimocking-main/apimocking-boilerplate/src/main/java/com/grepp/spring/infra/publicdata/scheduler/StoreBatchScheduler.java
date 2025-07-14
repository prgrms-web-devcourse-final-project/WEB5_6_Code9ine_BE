package com.grepp.spring.infra.publicdata.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!mock")
public class StoreBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job storeJob;

    @Scheduled(cron = "0 0 0 1 * *")
    public void runMonthly() throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(storeJob, jobParameters);
    }
}
