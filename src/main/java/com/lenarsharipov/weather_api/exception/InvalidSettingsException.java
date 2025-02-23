package com.lenarsharipov.weather_api.exception;

public class InvalidSettingsException extends RuntimeException {
    public InvalidSettingsException(String message) {
        super(message);
    }
}
