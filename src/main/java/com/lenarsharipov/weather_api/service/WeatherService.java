package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.model.WeatherResponse;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a service that provides access to current weather information.
 * The service supports basic operations such as retrieving weather data for a given location.
 * The service is also {@link Stoppable}, meaning it can be shut down and restarted.
 * <p>
 * Implementations of this interface must ensure that weather data is always up-to-date.
 * The service should also be thread-safe and handle concurrent requests correctly.
 * <p>
 * The service is designed to be used in a variety of scenarios, including web applications,
 * command-line tools, and scheduled tasks.
 */
public interface WeatherService extends Stoppable {

    Integer DEFAULT_DATA_FRESHNESS_PERIOD = 10;

    /**
     * Retrieves weather data for the specified location.
     * If the data is already in the cache and is fresh, it is returned from the cache.
     * Otherwise, the data is fetched from the external API and cached.
     *
     * @param location the location for which to fetch weather data
     * @return the weather data
     * @throws HttpException            if an error occurs while making the request to the external API
     * @throws ServiceShutDownException if the service is shut down
     */
    WeatherResponse getWeather(String location) throws HttpException;

    /**
     * Checks if the provided weather data is fresh (i.e., was received in the last 10 minutes).
     *
     * @param dt the time when the weather data was received
     * @return true if the data is fresh, false otherwise
     */
    default boolean isDataFresh(long dt, long period) {
        Instant dataTime = Instant.ofEpochSecond(dt);
        return Duration.between(dataTime, Instant.now()).toMinutes() < period;
    }
}
