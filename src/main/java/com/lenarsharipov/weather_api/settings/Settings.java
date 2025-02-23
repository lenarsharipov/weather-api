package com.lenarsharipov.weather_api.settings;

import java.util.concurrent.TimeUnit;

import static com.lenarsharipov.weather_api.datastructure.Cache.DEFAULT_CACHE_SIZE;
import static com.lenarsharipov.weather_api.service.WeatherService.DEFAULT_DATA_FRESHNESS_PERIOD;
import static com.lenarsharipov.weather_api.service.impl.WeatherServicePolling.*;

/**
 * A configuration class for the weather API.
 *
 * <p>This class contains settings for the data freshness period, cache size, polling period, initial delay, and time unit.
 *
 * <p>Settings can be configured using the {@link Builder}.
 *
 * @author Lenar Sharipov
 */
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

        /**
         * Sets the data freshness period.
         * @param dataFreshnessPeriod the data freshness period in seconds.
         * @return this builder.
         */
        public Builder dataFreshnessPeriod(Integer dataFreshnessPeriod) {
            this.dataFreshnessPeriod = dataFreshnessPeriod;
            return this;
        }

        /**
         * Sets the cache size.
         * @param cache the cache size.
         * @return this builder.
         */
        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Sets the polling settings.
         * @param polling the polling settings.
         * @return this builder.
         */
        public Builder polling(Polling polling) {
            this.polling = polling;
            return this;
        }

        /**
         * Builds the settings.
         * @return the settings.
         */
        public Settings build() {
            return new Settings(dataFreshnessPeriod, cache, polling);
        }
    }

    public record Cache(Integer size) {
        /**
         * A builder for the cache settings.
         */
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer size = DEFAULT_CACHE_SIZE;

            /**
             * Sets the cache size.
             * @param size the cache size.
             * @return this builder.
             */
            public Builder size(Integer size) {
                this.size = size;
                return this;
            }

            /**
             * Builds the cache settings.
             * @return the cache settings.
             */
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

            /**
             * Sets the initial delay.
             * @param initialDelay the initial delay in the given unit.
             * @return this builder.
             */
            public Builder initialDelay(Integer initialDelay) {
                this.initialDelay = initialDelay;
                return this;
            }

            /**
             * Sets the polling period.
             * @param period the polling period in the given unit.
             * @return this builder.
             */
            public Builder period(Integer period) {
                this.period = period;
                return this;
            }

            /**
             * Sets the time unit.
             * @param unit the time unit for the polling period and initial delay.
             * @return this builder.
             */
            public Builder unit(TimeUnit unit) {
                this.unit = unit;
                return this;
            }

            /**
             * Builds the polling settings.
             * @return the polling settings.
             */
            public Polling build() {
                return new Polling(initialDelay, period, unit);
            }
        }
    }
}