package com.lenarsharipov.weather_api.model;

public record Weather(
        long id,
        String main,
        String description,
        String icon
) {
}
