package com.lenarsharipov.weather_api.settings;

import java.util.concurrent.TimeUnit;

import static com.lenarsharipov.weather_api.datastructure.Cache.DEFAULT_CACHE_SIZE;
import static com.lenarsharipov.weather_api.service.WeatherService.DEFAULT_DATA_FRESHNESS_PERIOD;
import static com.lenarsharipov.weather_api.service.impl.WeatherServicePolling.*;

public record Settings(
        Integer dataFreshnessPeriod,
        Cache cache,
        Polling polling
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer dataFreshnessPeriod = DEFAULT_DATA_FRESHNESS_PERIOD;
        private Cache cache = Cache.builder().build(); // Используем дефолтный Cache
        private Polling polling = Polling.builder().build(); // Используем дефолтный Polling

        public Builder dataFreshnessPeriod(Integer dataFreshnessPeriod) {
            this.dataFreshnessPeriod = dataFreshnessPeriod;
            return this;
        }

        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder polling(Polling polling) {
            this.polling = polling;
            return this;
        }

        public Settings build() {
            return new Settings(dataFreshnessPeriod, cache, polling);
        }
    }

    public record Cache(Integer size) {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer size = DEFAULT_CACHE_SIZE;

            public Builder size(Integer size) {
                this.size = size;
                return this;
            }

            public Cache build() {
                return new Cache(size);
            }
        }
    }

    public record Polling(
            Integer initialDelay,
            Integer period,
            TimeUnit unit
    ) {
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer initialDelay = DEFAULT_POLLING_INITIAL_DELAY;
            private Integer period = DEFAULT_POLLING_PERIOD;
            private TimeUnit unit = DEFAULT_POLLING_TIME_UNIT;

            public Builder initialDelay(Integer initialDelay) {
                this.initialDelay = initialDelay;
                return this;
            }

            public Builder period(Integer period) {
                this.period = period;
                return this;
            }

            public Builder unit(TimeUnit unit) {
                this.unit = unit;
                return this;
            }

            public Polling build() {
                return new Polling(initialDelay, period, unit);
            }
        }
    }
}