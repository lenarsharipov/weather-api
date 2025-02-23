package com.lenarsharipov.weather_api.exception;

/**
 * An exception that is thrown when a service is created with invalid settings.
 * The exception is thrown by the {@link com.lenarsharipov.weather_api.validation.SettingsValidator}.
 * <p>
 * The exception is thrown when the settings object is null, or when any of the settings
 * (data freshness period, cache size, polling settings) are invalid.
 * <p>
 * The exception is unchecked, so it does not need to be declared in the throws clause.
 * However, it is a good practice to handle it in the code.
 */
public class InvalidSettingsException extends RuntimeException {
    public InvalidSettingsException(String message) {
        super(message);
    }
}
