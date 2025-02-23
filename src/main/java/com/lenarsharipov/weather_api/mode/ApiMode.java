package com.lenarsharipov.weather_api.mode;

import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.service.WeatherServiceOnDemand;
import com.lenarsharipov.weather_api.service.WeatherServicePolling;

import java.util.function.BiFunction;

/**
 * Represents the mode of operation for the weather service.
 * Each mode corresponds to a specific implementation of the WeatherService.
 */
public enum ApiMode {

    /**
     * Polling mode: periodically updates weather data for all stored locations.
     */
    POLLING(WeatherServicePolling::new),

    /**
     * On-demand mode: updates weather data only when explicitly requested.
     */
    ON_DEMAND(WeatherServiceOnDemand::new);

    private final BiFunction<String, WeatherHttpClient, WeatherService> serviceFactory;

    /**
     * Constructor for ApiMode.
     *
     * @param serviceFactory a factory function to create a WeatherService instance
     */
    ApiMode(BiFunction<String, WeatherHttpClient, WeatherService> serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    /**
     * Creates a new instance of the WeatherService based on the current mode.
     *
     * @param apiKey    the API key for the weather service
     * @param httpClient the HTTP client to be used by the service
     * @return a new instance of WeatherService
     */
    public WeatherService createWeatherService(String apiKey, WeatherHttpClient httpClient) {
        return serviceFactory.apply(apiKey, httpClient);
    }
}