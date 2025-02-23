package com.lenarsharipov.weather_api.service;

/**
 * Represents a service that can be stopped.
 */
public interface Stoppable {
    /**
     * Shuts down the service. After calling this method, all requests will be rejected.
     */
    void shutdown();
}
