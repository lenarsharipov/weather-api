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

public final class WeatherServiceFactory {

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final Map<String, WeatherService> services = new ConcurrentHashMap<>();
    private static final WeatherHttpClient httpClient =
            new WeatherHttpClient(HttpClient.newHttpClient(), new ObjectMapper(), API_URL);

    private WeatherServiceFactory() {
    }

    public static WeatherService getWeatherService(String apiKey, ApiMode apiMode) {
        Settings defaultSettings = Settings.builder().build();
        return getWeatherService(apiKey, apiMode, defaultSettings);
    }

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

    public static void removeWeatherService(String apiKey) {
        WeatherService service = services.remove(apiKey);
        if (service == null) {
            String msg = String.format("No service found for apiKey: %s", apiKey);
            throw new ServiceNotFoundException(msg);
        }
        service.shutdown();
    }
}
