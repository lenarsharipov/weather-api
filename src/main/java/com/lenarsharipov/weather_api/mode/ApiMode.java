package com.lenarsharipov.weather_api.mode;

import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.service.impl.WeatherServiceOnDemand;
import com.lenarsharipov.weather_api.service.impl.WeatherServicePolling;
import com.lenarsharipov.weather_api.settings.Settings;
import com.lenarsharipov.weather_api.utils.TriFunction;

/**
 * Represents the mode of the API.
 * The mode determines how the API will behave, such as how often it will fetch data from the external source.
 * The API can be either in polling mode, where it fetches data at a regular interval,
 * or in on-demand mode, where it fetches data only when explicitly requested.
 * <p>
 * The API will always return the latest data available, regardless of the mode.
 * <p>
 * The polling mode is more suitable for applications that require up-to-date data,
 * while the on-demand mode is more suitable for applications that only need data occasionally.
 */
public enum ApiMode {

    /**
     * Polling mode.
     * In this mode, the API will fetch data from the external source at a regular interval.
     */
    POLLING(WeatherServicePolling::new),

    /**
     * On-demand mode.
     * In this mode, the API will fetch data from the external source only when explicitly requested.
     */
    ON_DEMAND(WeatherServiceOnDemand::new);

    private final TriFunction<String, WeatherHttpClient, Settings, WeatherService> serviceFactory;

    /**
     * @param serviceFactory the factory that creates the service instance
     */
    ApiMode(TriFunction<String, WeatherHttpClient, Settings, WeatherService> serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    /**
     * Creates a new instance of the service.
     *
     * @param apiKey     the API key to use
     * @param httpClient the HTTP client to use
     * @param settings   the settings to use
     * @return the created service instance
     */
    public WeatherService createWeatherService(String apiKey,
                                               WeatherHttpClient httpClient,
                                               Settings settings) {
        return serviceFactory.apply(apiKey, httpClient, settings);
    }
}