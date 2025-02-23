package com.lenarsharipov.weather_api.model;

import java.util.List;

public record WeatherResponse(
        Coord coord,
        List<Weather> weather,
        String base,
        Main main,
        int visibility,
        Wind wind,
        Clouds clouds,
        Rain rain,
        Snow snow,
        long dt,
        Sys sys,
        int timezone,
        int id,
        String name,
        int cod
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Coord coord;
        private List<Weather> weather;
        private String base;
        private Main main;
        private int visibility;
        private Wind wind;
        private Clouds clouds;
        private Rain rain;
        private Snow snow;
        private long dt;
        private Sys sys;
        private int timezone;
        private int id;
        private String name;
        private int cod;

        public Builder coord(Coord coord) {
            this.coord = coord;
            return this;
        }

        public Builder weather(List<Weather> weather) {
            this.weather = weather;
            return this;
        }

        public Builder base(String base) {
            this.base = base;
            return this;
        }

        public Builder main(Main main) {
            this.main = main;
            return this;
        }

        public Builder visibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder wind(Wind wind) {
            this.wind = wind;
            return this;
        }

        public Builder clouds(Clouds clouds) {
            this.clouds = clouds;
            return this;
        }

        public Builder rain(Rain rain) {
            this.rain = rain;
            return this;
        }

        public Builder snow(Snow snow) {
            this.snow = snow;
            return this;
        }

        public Builder dt(long dt) {
            this.dt = dt;
            return this;
        }

        public Builder sys(Sys sys) {
            this.sys = sys;
            return this;
        }

        public Builder timezone(int timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder cod(int cod) {
            this.cod = cod;
            return this;
        }

        public WeatherResponse build() {
            return new WeatherResponse(coord, weather, base, main, visibility, wind, clouds, rain, snow, dt, sys, timezone, id, name, cod);
        }
    }
}
