package com.lenarsharipov.weather_api.exception;

/**
 * Exception thrown when no service is found for the given apiKey.
 */
public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
