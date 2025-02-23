package com.lenarsharipov.weather_api.exception;

/**
 * An exception that is thrown when a service with the same apiKey
 * already exists in the cache of WeatherServiceFactory.
 */
public class ServiceExistsException extends RuntimeException {
    public ServiceExistsException(String message) {
        super(message);
    }
}
