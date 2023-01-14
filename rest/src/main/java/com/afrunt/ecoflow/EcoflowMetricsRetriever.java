package com.afrunt.ecoflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class EcoflowMetricsRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(EcoflowMetricsRetriever.class);
    private final RestTemplate restTemplate;

    public EcoflowMetricsRetriever(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "metrics", key = "#serialNumber")
    public Optional<MetricResponse> getMetrics(String serialNumber, String appKey, String secretKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appKey", appKey);
        headers.set("secretKey", secretKey);
        ResponseEntity<MetricResponse> responseEntity = restTemplate.exchange("https://api.ecoflow.com/iot-service/open/api/device/queryDeviceQuota?sn=%s".formatted(serialNumber), HttpMethod.GET, new HttpEntity<>("body", headers), MetricResponse.class);
        LOGGER.debug("Metrics retrieved");
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(responseEntity.getBody());
        } else {
            return Optional.empty();
        }
    }
}
