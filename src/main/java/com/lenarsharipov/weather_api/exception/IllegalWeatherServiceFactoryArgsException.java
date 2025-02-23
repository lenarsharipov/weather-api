package com.lenarsharipov.weather_api.exception;

public class IllegalWeatherServiceFactoryArgsException extends RuntimeException {
    public IllegalWeatherServiceFactoryArgsException(String message) {
        super(message);
    }
}
