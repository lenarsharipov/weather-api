package com.lenarsharipov.weather_api.service.impl;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.service.AbstractWeatherService;
import com.lenarsharipov.weather_api.settings.Settings;

/**
 * A weather service that fetches weather data on-demand.
 * This service updates weather data only when explicitly requested.
 */
public class WeatherServiceOnDemand extends AbstractWeatherService {

    /**
     * Constructs a new WeatherServiceOnDemand instance.
     *
     * @param apiKey     the API key for authentication
     * @param httpClient the HTTP client used to fetch weather data
     * @param settings   the service settings
     */
    public WeatherServiceOnDemand(String apiKey,
                                  WeatherHttpClient httpClient,
                                  Settings settings) {
        super(apiKey, httpClient, settings);
    }

    /**
     * Fetches weather data for the specified location.
     *
     * @param location the location to fetch weather data for
     * @return the weather response
     * @throws HttpException if an error occurs during data retrieval
     */
    @Override
    protected WeatherResponse fetchWeather(String location) throws HttpException {
        return httpClient.getWeather(location, apiKey);
    }
}
