package com.afrunt.ecoflow;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ecoflow")
public class EcoflowProperties {
    private Map<String, Device> devices;

    public Map<String, Device> getDevices() {
        return devices;
    }

    public EcoflowProperties setDevices(Map<String, Device> devices) {
        this.devices = devices;
        return this;
    }

    public record Device(String serialNumber, String appKey, String secretKey, String name) {

    }
}
