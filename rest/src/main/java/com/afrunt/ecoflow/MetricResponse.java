package com.afrunt.ecoflow;

public record MetricResponse(int code, String message, Data data) {
    public record Data(int soc, int remainTime, int wattsOutSum, int wattsInSum) {

    }
}
