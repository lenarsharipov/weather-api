package com.lenarsharipov.weather_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Snow(
        @JsonProperty("1h")
        double oneHour
) {
}
