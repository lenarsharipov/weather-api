package com.lenarsharipov.weather_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Main(
        double temp,
        @JsonProperty("feels_like")
        double feelsLike,
        int pressure,
        int humidity,
        @JsonProperty("temp_min")
        double tempMin,
        @JsonProperty("temp_max")
        double temp_max,
        @JsonProperty("sea_level")
        int seaLevel,
        @JsonProperty("grnd_level")
        int groundLevel
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double temp;
        private double feelsLike;
        private int pressure;
        private int humidity;
        private double tempMin;
        private double tempMax;
        private int seaLevel;
        private int groundLevel;

        public Builder temp(double temp) {
            this.temp = temp;
            return this;
        }

        public Builder feelsLike(double feelsLike) {
            this.feelsLike = feelsLike;
            return this;
        }

        public Builder pressure(int pressure) {
            this.pressure = pressure;
            return this;
        }

        public Builder humidity(int humidity) {
            this.humidity = humidity;
            return this;
        }

        public Builder tempMin(double tempMin) {
            this.tempMin = tempMin;
            return this;
        }

        public Builder tempMax(double tempMax) {
            this.tempMax = tempMax;
            return this;
        }

        public Builder seaLevel(int seaLevel) {
            this.seaLevel = seaLevel;
            return this;
        }

        public Builder groundLevel(int groundLevel) {
            this.groundLevel = groundLevel;
            return this;
        }

        public Main build() {
            return new Main(temp, feelsLike, pressure, humidity, tempMin, tempMax, seaLevel, groundLevel);
        }
    }
}
