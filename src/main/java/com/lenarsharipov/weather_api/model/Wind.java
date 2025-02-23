package com.lenarsharipov.weather_api.model;

public record Wind(
        double speed,
        int deg,
        double gust
) {
}
