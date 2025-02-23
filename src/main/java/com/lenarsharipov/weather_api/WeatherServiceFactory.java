package com.lenarsharipov.weather_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.weather_api.exception.IllegalWeatherServiceFactoryArgsException;
import com.lenarsharipov.weather_api.exception.ServiceExistsException;
import com.lenarsharipov.weather_api.exception.ServiceNotFoundException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.mode.ApiMode;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.settings.Settings;
import com.lenarsharipov.weather_api.validation.SettingsValidator;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A factory class for creating instances of WeatherService.
 * <p>
 * This class provides the ability to create a WeatherService with a given API key and mode.
 * The mode determines the type of service to create: polling or on-demand.
 * <p>
 * The factory caches the created services, so that subsequent calls with the same API key will return
 * the same instance.
 * <p>
 * The factory also provides a way to remove a service from the cache.
 */
public final class WeatherServiceFactory {

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final Map<String, WeatherService> services = new ConcurrentHashMap<>();
    private static final WeatherHttpClient httpClient =
            new WeatherHttpClient(HttpClient.newHttpClient(), new ObjectMapper(), API_URL);

    private WeatherServiceFactory() {
    }

    /**
     * Returns a WeatherService instance for the given API key and mode.
     * If the service doesn't exist yet, it is created with the default settings.
     * <p>
     * The factory caches the created services, so that subsequent calls with the same API key will
     * return the same instance.
     * <p>
     * If there is already a service for the given API key, a ServiceExistsException is thrown.
     *
     * @param apiKey  the API key for the service
     * @param apiMode the mode for the service
     * @return the WeatherService instance
     * @throws IllegalWeatherServiceFactoryArgsException if the passed args are invalid
     * @throws ServiceExistsException                    if a service with the same API key already exists
     */
    public static WeatherService getWeatherService(String apiKey, ApiMode apiMode) {
        Settings defaultSettings = Settings.builder().build();
        return getWeatherService(apiKey, apiMode, defaultSettings);
    }

    /**
     * Returns a WeatherService instance for the given API key, mode and settings.
     * If the service doesn't exist yet, it is created with the given settings.
     * <p>
     * The factory caches the created services, so that subsequent calls with the same API key will
     * return the same instance.
     * <p>
     * If there is already a service for the given API key, a ServiceExistsException is thrown.
     *
     * @param apiKey  the API key for the service
     * @param apiMode the mode for the service
     * @param settings the settings for the service
     * @return the WeatherService instance
     * @throws IllegalWeatherServiceFactoryArgsException if the passed args are invalid
     * @throws ServiceExistsException                    if a service with the same API key already exists
     */
    public static WeatherService getWeatherService(String apiKey,
                                                   ApiMode apiMode,
                                                   Settings settings) {
        if (apiKey == null || apiKey.isBlank() || apiMode == null) {
            throw new IllegalWeatherServiceFactoryArgsException("Passed args cannot be null or empty");
        }
        if (services.containsKey(apiKey)) {
            String msg = String.format("There is already a service for apiKey: %s", apiKey);
            throw new ServiceExistsException(msg);
        }
        SettingsValidator.validate(settings);
        return services.computeIfAbsent(apiKey,
                key -> apiMode.createWeatherService(key, httpClient, settings));
    }

    /**
     * Removes the service for the given API key from the cache.
     * <p>
     * If there is no service for the given API key, a ServiceNotFoundException is thrown.
     *
     * @param apiKey the API key for the service
     * @throws ServiceNotFoundException if there is no service for the given API key
     */
    public static void removeWeatherService(String apiKey) {
        WeatherService service = services.remove(apiKey);
        if (service == null) {
            String msg = String.format("No service found for apiKey: %s", apiKey);
            throw new ServiceNotFoundException(msg);
        }
        service.shutdown();
    }
}
