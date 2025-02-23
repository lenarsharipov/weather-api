package com.lenarsharipov.weather_api;

import com.lenarsharipov.weather_api.exception.ServiceExistsException;
import com.lenarsharipov.weather_api.exception.ServiceNotFoundException;
import com.lenarsharipov.weather_api.mode.ApiMode;
import com.lenarsharipov.weather_api.service.WeatherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("WeatherServiceFactory")
@ExtendWith(MockitoExtension.class)
class WeatherServiceFactoryTest {

    private final String apiKey = "test-api-key";
    private final ApiMode apiMode = ApiMode.ON_DEMAND;

    @Test
    @DisplayName("creates a new service for a new apiKey")
    void shouldCreateNewServiceForNewApiKey() {
        WeatherService service = WeatherServiceFactory.getWeatherService(apiKey, apiMode);
        assertNotNull(service);
        WeatherServiceFactory.removeWeatherService(apiKey);
    }

    @Test
    @DisplayName("throws ServiceExistsException if service for apiKey already exists")
    void shouldThrowServiceExistsExceptionIfServiceAlreadyExists() {
        WeatherService service = WeatherServiceFactory.getWeatherService(apiKey, apiMode);
        assertThrows(ServiceExistsException.class, () -> WeatherServiceFactory.getWeatherService(apiKey, apiMode));
        WeatherServiceFactory.removeWeatherService(apiKey);
    }

    @Test
    @DisplayName("throws ServiceNotFoundException")
    void shouldThrowServiceNotFoundExceptionWhenToRemoveNotExistingService() {
        assertThrows(ServiceNotFoundException.class, () -> WeatherServiceFactory.removeWeatherService(apiKey));
    }
}