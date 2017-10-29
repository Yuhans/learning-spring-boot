package com.yuhans.learningspringboot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.yuhans.learningspringboot")
@EntityScan("com.yuhans.learningspringboot.domain")
@EnableJpaRepositories("com.yuhans.learningspringboot.repository")
public class LearningSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningSpringBootApplication.class, args);
	}

	@Bean
    InMemoryMetricRepository inMemoryMetricRepository() {
	    return new InMemoryMetricRepository();
    }

    @Bean
    PublicMetrics publicMetrics(InMemoryMetricRepository inMemoryMetricRepository) {
	    return new MetricReaderPublicMetrics(inMemoryMetricRepository);
    }
}
