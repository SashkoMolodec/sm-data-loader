package com.sashkomusic.dataloader.config;

import com.sashkomusic.dataloader.SMusicDataLoaderApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.ThreadWaitSleeper;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

@Configuration
public class AiClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(SMusicDataLoaderApplication.class);

    @Bean
    public RetryTemplate retryTemplate(SpringAiRetryProperties properties) {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getBackoff().getInitialInterval().toMillis());
        backOffPolicy.setMultiplier(properties.getBackoff().getMultiplier());
        backOffPolicy.setMaxInterval(properties.getBackoff().getMaxInterval().toMillis());
        backOffPolicy.setSleeper(new ThreadWaitSleeper() {
            @Override
            public void sleep(long backOffPeriod) throws InterruptedException {
                try {
                    Thread.sleep(backOffPeriod);
                } catch (InterruptedException e) {
                    logger.warn("Sleep interrupted, continuing...");
                }
            }
        });

        return RetryTemplate.builder()
                .maxAttempts(properties.getMaxAttempts())
                .retryOn(InterruptedException.class)
                .retryOn(TransientAiException.class)
                .retryOn(ResourceAccessException.class)
                .retryOn(IOException.class)
                .customBackoff(backOffPolicy)
                .withListener(new RetryListener() {
                    @Override
                    public <T, E extends Throwable> void onError(
                            RetryContext context,
                            RetryCallback<T, E> callback,
                            Throwable throwable
                    ) {
                        logger.warn("Retry error. Retry count: {}, Exception: {}",
                                context.getRetryCount(),
                                throwable.getMessage(),
                                throwable
                        );
                    }
                })
                .build();
    }
}
