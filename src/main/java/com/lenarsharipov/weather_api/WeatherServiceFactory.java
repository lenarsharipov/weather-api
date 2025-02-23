package com.lenarsharipov.weather_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.weather_api.exception.ServiceExistsException;
import com.lenarsharipov.weather_api.exception.ServiceNotFoundException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.mode.ApiMode;
import com.lenarsharipov.weather_api.service.WeatherService;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A factory class to create instances of {@link WeatherService}. The service is bound to the provided API key
 * and the mode of operation. The service is stored in a map and reused for the same API key and mode.
 */
public final class WeatherServiceFactory {

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final Map<String, WeatherService> services = new ConcurrentHashMap<>();
    private static final WeatherHttpClient httpClient =
            new WeatherHttpClient(HttpClient.newHttpClient(), new ObjectMapper(), API_URL);

    private WeatherServiceFactory() {
    }

    /**
     * Returns a WeatherService instance for the given apiKey and apiMode.
     * If a service with the same apiKey already exists, throws ServiceExistsException.
     *
     * @param apiKey the API key for the weather service
     * @param apiMode the mode of operation for the service
     * @return a WeatherService instance
     * @throws ServiceExistsException if a service with the same apiKey already exists
     */
    public static WeatherService getWeatherService(String apiKey, ApiMode apiMode) {
        if (services.containsKey(apiKey)) {
            String msg = String.format("There is already a service for apiKey: %s", apiKey);
            throw new ServiceExistsException(msg);
        }
        return services.computeIfAbsent(apiKey,
                key -> apiMode.createWeatherService(key, httpClient));
    }

    /**
     * Removes a WeatherService instance for the given apiKey.
     * If no service with the apiKey exists, throws ServiceNotFoundException.
     * If a service with the same apiKey exists, it is shut down and removed from the cache.
     *
     * @param apiKey the API key for the weather service
     * @throws ServiceNotFoundException if no service with the same apiKey exists
     */
    public static void removeWeatherService(String apiKey) {
        WeatherService service = services.remove(apiKey);
        if (service == null) {
            String msg = String.format("No service found for apiKey: %s", apiKey);
            throw new ServiceNotFoundException(msg);
        }
        // Shut down the service before removing it from the cache
        service.shutdown();
    }
}
