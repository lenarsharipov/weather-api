package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.datastructure.Cache;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.settings.Settings;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract base class for weather services providing common functionality.
 * Implements basic operations such as shutdown and data retrieval with caching.
 */
public abstract class AbstractWeatherService implements WeatherService {

    protected final AtomicBoolean isActive = new AtomicBoolean(true);
    protected final Integer dataFreshnessPeriod;
    protected final String apiKey;
    protected final WeatherHttpClient httpClient;
    protected final Cache cache;

    /**
     * Initializes the weather service with the provided API key, HTTP client, and settings.
     *
     * @param apiKey       the API key for the weather service
     * @param httpClient   the HTTP client for API communication
     * @param settings     the settings for the service configuration
     */
    public AbstractWeatherService(String apiKey,
                                  WeatherHttpClient httpClient,
                                  Settings settings) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.cache = new Cache(settings.cache().size());
        this.dataFreshnessPeriod = settings.dataFreshnessPeriod() == null
                ? DEFAULT_DATA_FRESHNESS_PERIOD
                : settings.dataFreshnessPeriod();
    }

    /**
     * Shuts down the weather service, clearing the cache and marking it as inactive.
     */
    @Override
    public void shutdown() {
        this.isActive.set(false);
        this.cache.clear();
    }

    /**
     * Retrieves weather data for the specified location. Uses cache if data is fresh.
     *
     * @param location the location for which to fetch weather data
     * @return the weather data
     * @throws HttpException if an error occurs during data retrieval
     */
    @Override
    public WeatherResponse getWeather(String location) throws HttpException {
        if (!isActive.get()) {
            throw new ServiceShutDownException();
        }

        WeatherResponse weatherResponse = cache.get(location);
        if (weatherResponse != null && isDataFresh(weatherResponse.dt(), dataFreshnessPeriod)) {
            return weatherResponse;
        }
        weatherResponse = fetchWeather(location);
        cache.put(location, weatherResponse);
        return weatherResponse;
    }

    /**
     * Fetches weather data for the specified location from the external source.
     *
     * @param location the location to fetch weather data for
     * @return the weather data
     * @throws HttpException if an error occurs during data retrieval
     */
    protected abstract WeatherResponse fetchWeather(String location) throws HttpException;
}
