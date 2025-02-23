package com.lenarsharipov.weather_api;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.mode.ApiMode;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.settings.Settings;

public class Main {
    public static void main(String[] args) throws HttpException, InterruptedException {
        WeatherService weatherService = WeatherServiceFactory.getWeatherService(
                "929387b00c7948f6cc6ec6438a95369a",
                ApiMode.ON_DEMAND
        );

        System.out.println(weatherService.getWeather("San Francisco"));
        System.out.println(weatherService.getWeather("Saint Petersburg"));
        System.out.println(weatherService.getWeather("Antigua"));
        System.out.println(weatherService.getWeather("Paris"));
        System.out.println(weatherService.getWeather("London"));
        System.out.println(weatherService.getWeather("Memphis"));
        Thread.sleep(10000);
        System.out.println(weatherService.getWeather("San Francisco"));
        System.out.println(weatherService.getWeather("Saint Petersburg"));
        System.out.println(weatherService.getWeather("Antigua"));
        System.out.println(weatherService.getWeather("Paris"));
        System.out.println(weatherService.getWeather("London"));
        System.out.println(weatherService.getWeather("Memphis"));
    }
}
