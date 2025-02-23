package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.datastructure.Cache;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a weather service that periodically polls and updates weather data
 * for all cached locations. This service ensures that weather data is always up-to-date
 * and minimizes latency for client requests by maintaining a fresh cache.
 */
public class WeatherServicePolling implements WeatherService {

    private final static Logger logger = LoggerFactory.getLogger(WeatherServicePolling.class);

    private final String apiKey;

    private final WeatherHttpClient httpClient;

    private final Cache cache;

    private final ScheduledExecutorService scheduler;

    /**
     * Flag indicating whether the service is active. If the service is shut down,
     * all requests and polling will be stopped.
     */
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    /**
     * Creates a new instance of the polling weather service.
     *
     * @param apiKey     the API key for accessing the external API.
     * @param httpClient the HTTP client for making requests.
     */
    public WeatherServicePolling(String apiKey, WeatherHttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;

        this.cache = new Cache();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        startPolling();
    }

    /**
     * Shuts down the service. After calling this method, all requests will be rejected,
     * and the polling process will be stopped.
     */
    @Override
    public void shutdown() {
        this.isActive.set(false);
        scheduler.shutdownNow();
        this.cache.clear();
    }

    /**
     * Retrieves weather data for the specified location.
     * If the data is already in the cache and is fresh, it is returned from the cache.
     * Otherwise, the data is fetched from the external API and cached.
     *
     * @param location the location for which to fetch weather data.
     * @return the weather data.
     * @throws HttpException            if an error occurs while making the request to the external API.
     * @throws ServiceShutDownException if the service is shut down.
     */
    @Override
    public WeatherResponse getWeather(String location) throws HttpException {
        if (!isActive.get()) {
            throw new ServiceShutDownException();
        }

        WeatherResponse weatherResponse = cache.get(location);
        if (weatherResponse == null || !isDataFresh(weatherResponse.dt())) {
            weatherResponse = httpClient.getWeather(location, apiKey);
            cache.put(location, weatherResponse);
        }

        return weatherResponse;
    }

    /**
     * Starts the polling process to periodically update weather data for all cached locations.
     * The polling runs every 5 seconds.
     */
    private void startPolling() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isActive.get()) return;

            try {
                updateLocationsWeather();
            } catch (Exception e) {
                logger.error("Critical polling error: {}", e.getMessage());
                shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Updates weather data for all locations currently stored in the cache.
     * If the data for a location is stale or missing, it is fetched from the external API
     * and updated in the cache.
     */
    private void updateLocationsWeather() {
        Set<String> locations = cache.getLocations();
        for (String location : locations) {
            try {
                WeatherResponse cachedWeather = httpClient.getWeather(location, apiKey);
                if (cachedWeather == null || !isDataFresh(cachedWeather.dt())) {
                    WeatherResponse updatedWeather = httpClient.getWeather(location, apiKey);
                    cache.put(location, updatedWeather);
                }
            } catch (HttpException e) {
                logger.error("Failed to update location {}: {}", location, e.getMessage());
            }
        }
    }
}
