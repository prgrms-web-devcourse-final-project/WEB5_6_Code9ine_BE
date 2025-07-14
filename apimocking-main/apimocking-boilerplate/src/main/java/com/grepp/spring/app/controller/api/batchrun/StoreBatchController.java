package com.grepp.spring.app.controller.api.batchrun;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class StoreBatchController {

    private final JobLauncher jobLauncher;
    private final Job storeJob;

    @GetMapping("/store")
    @Operation(summary = "식당 DB 받아오기")
    public String runStoreBatch() throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(storeJob, jobParameters);
        return "success";
    }
}