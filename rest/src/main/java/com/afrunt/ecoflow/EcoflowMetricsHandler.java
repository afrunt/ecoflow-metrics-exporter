package com.afrunt.ecoflow;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class EcoflowMetricsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EcoflowMetricsHandler.class);

    private final EcoflowMetricsRetriever ecoflowMetricsRetriever;
    private final MeterRegistry meterRegistry;

    private final EcoflowProperties ecoflowProperties;

    private final CacheManager cacheManager;

    public EcoflowMetricsHandler(EcoflowMetricsRetriever ecoflowMetricsRetriever, MeterRegistry meterRegistry, EcoflowProperties ecoflowProperties, CacheManager cacheManager) {
        this.ecoflowMetricsRetriever = ecoflowMetricsRetriever;
        this.meterRegistry = meterRegistry;
        this.ecoflowProperties = ecoflowProperties;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        if (ecoflowProperties.getDevices().isEmpty()) {
            LOGGER.error("You should provide the configuration of your devices");
            System.exit(1);
        }

        ecoflowProperties.getDevices().values().forEach(device -> {
            registerGauge("wattsOutSum", "Output Watts", MetricResponse.Data::wattsOutSum, device);
            registerGauge("wattsInSum", "Input Watts", MetricResponse.Data::wattsInSum, device);
            registerGauge("remainTime", "Remain Time Minutes", MetricResponse.Data::remainTime, device);
        });
    }

    private void registerGauge(String metricName, String description, Function<MetricResponse.Data, Number> mapper, EcoflowProperties.Device device) {
        Gauge.builder("ecoflow." + metricName, () -> deviceMetricNumber(device, mapper))
                .tag("device", device.name())
                .description(description)
                .register(meterRegistry);
    }

    private Number deviceMetricNumber(EcoflowProperties.Device device, Function<MetricResponse.Data, Number> mapper) {
        return ecoflowMetricsRetriever.getMetrics(device.serialNumber(), device.appKey(), device.secretKey())
                .map(m -> mapper.apply(m.data()))
                .orElse(null);
    }

    @Scheduled(fixedRate = 5000)
    public void evictAllCachesAtIntervals() {
        cacheManager.getCacheNames().forEach(n -> Objects.requireNonNull(cacheManager.getCache(n)).clear());
        LOGGER.debug("Caches cleared");
    }
}
