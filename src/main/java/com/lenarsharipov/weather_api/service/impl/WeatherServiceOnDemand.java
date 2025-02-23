package com.lenarsharipov.weather_api.service.impl;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.service.AbstractWeatherService;
import com.lenarsharipov.weather_api.settings.Settings;

public class WeatherServiceOnDemand extends AbstractWeatherService {

    public WeatherServiceOnDemand(String apiKey,
                                  WeatherHttpClient httpClient,
                                  Settings settings) {
        super(apiKey, httpClient, settings);
    }

    @Override
    protected WeatherResponse fetchWeather(String location) throws HttpException {
        return httpClient.getWeather(location, apiKey);
    }
}
