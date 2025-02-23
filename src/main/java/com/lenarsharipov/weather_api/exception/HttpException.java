package com.lenarsharipov.weather_api.exception;

/**
 * Exception thrown by the HTTP client when a network error occurs.
 */
public class HttpException extends Exception {
    private final int statusCode;

    public HttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
