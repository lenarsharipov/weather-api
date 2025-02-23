package com.lenarsharipov.weather_api.model;

public record Sys(
        long id,
        int type,
        String message,
        String country,
        long sunrise,
        long sunset
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long id;
        private int type;
        private String message;
        private String country;
        private long sunrise;
        private long sunset;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder sunrise(long sunrise) {
            this.sunrise = sunrise;
            return this;
        }

        public Builder sunset(long sunset) {
            this.sunset = sunset;
            return this;
        }

        public Sys build() {
            return new Sys(id, type, message, country, sunrise, sunset);
        }
    }
}
