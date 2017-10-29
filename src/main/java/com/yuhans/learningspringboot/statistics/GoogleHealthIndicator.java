package com.yuhans.learningspringboot.statistics;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class GoogleHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            int responseCode = ((HttpURLConnection) new URL("http://google.com").openConnection()).getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("HTTP status code", responseCode).build();
            }
        } catch (IOException e) {
            return Health.down(e).build();
        }
    }
}
