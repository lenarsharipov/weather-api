package com.lenarsharipov.weather_api.service.impl;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.service.AbstractWeatherService;
import com.lenarsharipov.weather_api.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WeatherServicePolling provides a scheduled weather data update service.
 * It periodically fetches weather data for all cached locations using a polling mechanism.
 */
public class WeatherServicePolling extends AbstractWeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherServicePolling.class);

    public static final Integer DEFAULT_POLLING_PERIOD = 5;
    public static final Integer DEFAULT_POLLING_INITIAL_DELAY = 0;
    public static final TimeUnit DEFAULT_POLLING_TIME_UNIT = TimeUnit.SECONDS;

    private final Integer pollingPeriod;
    private final Integer pollingInitialDelay;
    private final TimeUnit pollingTimeUnit;
    private final ScheduledExecutorService scheduler;

    /**
     * Constructs a WeatherServicePolling instance with specified API key, HTTP client, and settings.
     *
     * @param apiKey the API key for fetching weather data
     * @param httpClient the HTTP client to use for requests
     * @param settings the settings for polling behavior
     */
    public WeatherServicePolling(String apiKey,
                                 WeatherHttpClient httpClient,
                                 Settings settings) {
        super(apiKey, httpClient, settings);
        this.pollingPeriod = settings.polling().period() == null
                ? DEFAULT_POLLING_PERIOD
                : settings.polling().period();
        this.pollingInitialDelay = settings.polling().initialDelay() == null
                ? DEFAULT_POLLING_INITIAL_DELAY
                : settings.polling().initialDelay();
        this.pollingTimeUnit = settings.polling().unit() == null
                ? DEFAULT_POLLING_TIME_UNIT
                : settings.polling().unit();

        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        startPolling();
    }

    /**
     * Shuts down the polling service, stopping all scheduled tasks.
     */
    @Override
    public void shutdown() {
        super.shutdown();
        scheduler.shutdownNow();
    }

    /**
     * Fetches weather data for a given location from the external API.
     *
     * @param location the location for which to fetch weather data
     * @return the weather data response
     * @throws HttpException if an error occurs during the request
     */
    @Override
    protected WeatherResponse fetchWeather(String location) throws HttpException {
        return httpClient.getWeather(location, apiKey);
    }

    /**
     * Starts the polling mechanism to periodically update weather data.
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
        }, pollingInitialDelay, pollingPeriod, pollingTimeUnit);
    }

    /**
     * Updates the weather data for all cached locations.
     */
    private void updateLocationsWeather() {
        Set<String> locations = cache.getLocations();
        for (String location : locations) {
            try {
                WeatherResponse cachedWeather = httpClient.getWeather(location, apiKey);
                if (cachedWeather == null
                        || !isDataFresh(cachedWeather.dt(), dataFreshnessPeriod)) {
                    WeatherResponse updatedWeather = httpClient.getWeather(location, apiKey);
                    cache.put(location, updatedWeather);
                }
            } catch (HttpException e) {
                logger.error("Failed to update location {}: {}", location, e.getMessage());
            }
        }
    }
}
