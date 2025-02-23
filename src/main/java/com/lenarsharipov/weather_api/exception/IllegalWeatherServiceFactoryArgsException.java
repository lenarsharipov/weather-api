package com.lenarsharipov.weather_api.exception;

/**
 * An exception that is thrown when attempting to create a WeatherService
 * with illegal arguments.
 */
public class IllegalWeatherServiceFactoryArgsException extends RuntimeException {
    public IllegalWeatherServiceFactoryArgsException(String message) {
        super(message);
    }
}