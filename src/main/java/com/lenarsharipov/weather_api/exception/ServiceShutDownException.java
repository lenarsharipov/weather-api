package com.lenarsharipov.weather_api.exception;

/**
 * An exception that is thrown when a service is shut down and an attempt is
 * made to use it.
 */
public class ServiceShutDownException extends RuntimeException {
    public ServiceShutDownException() {
        super("Service is shut down and cannot be used");
    }
}
