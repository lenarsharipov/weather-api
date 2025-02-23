package com.lenarsharipov.weather_api.mode;

import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.service.impl.WeatherServiceOnDemand;
import com.lenarsharipov.weather_api.service.impl.WeatherServicePolling;
import com.lenarsharipov.weather_api.settings.Settings;
import com.lenarsharipov.weather_api.utils.TriFunction;

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

    private final TriFunction<String, WeatherHttpClient, Settings, WeatherService> serviceFactory;

    ApiMode(TriFunction<String, WeatherHttpClient, Settings, WeatherService> serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public WeatherService createWeatherService(String apiKey,
                                               WeatherHttpClient httpClient,
                                               Settings settings) {
        return serviceFactory.apply(apiKey, httpClient, settings);
    }
}