package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.datastructure.Cache;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a weather service that fetches weather data on demand.
 * This service does not maintain a cache and always fetches weather data from the external API.
 * It is suitable for scenarios where the service is used infrequently or when the data is not often accessed.
 */
public class WeatherServiceOnDemand implements WeatherService {

    private final AtomicBoolean isActive = new AtomicBoolean(true);

    private final String apiKey;
    private final WeatherHttpClient httpClient;
    private final Cache cache;

    public WeatherServiceOnDemand(String apiKey, WeatherHttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.cache = new Cache();
    }

    /**
     * Implementation of a weather service that fetches weather data on demand.
     * This service does not maintain a polling mechanism and updates the weather data only
     * when explicitly requested by the client for a specific location.
     */
    @Override
    public WeatherResponse getWeather(String location) throws HttpException {
        if (!isActive.get()) {
            throw new ServiceShutDownException();
        }

        WeatherResponse weatherResponse = cache.get(location);
        if (weatherResponse != null && isDataFresh(weatherResponse.dt())) {
            return weatherResponse;
        }
        weatherResponse = httpClient.getWeather(location, apiKey);
        cache.put(location, weatherResponse);
        return weatherResponse;
    }


    /**
     * Shuts down the service. After calling this method, all requests will be rejected.
     */
    @Override
    public void shutdown() {
        this.isActive.set(false);
        this.cache.clear();
    }
}
