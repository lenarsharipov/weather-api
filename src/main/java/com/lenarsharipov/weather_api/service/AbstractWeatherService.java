package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.datastructure.Cache;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.settings.Settings;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractWeatherService implements WeatherService {

    protected final AtomicBoolean isActive = new AtomicBoolean(true);
    protected final Integer dataFreshnessPeriod;
    protected final String apiKey;
    protected final WeatherHttpClient httpClient;
    protected final Cache cache;

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

    @Override
    public void shutdown() {
        this.isActive.set(false);
        this.cache.clear();
    }

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
     * Fetches weather data from the external API for the specified location.
     * This method must be implemented by concrete subclasses.
     *
     * @param location the location for which to fetch weather data
     * @return the weather data
     * @throws HttpException if an error occurs while making the request to the external API
     */
    protected abstract WeatherResponse fetchWeather(String location) throws HttpException;
}
