package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.service.impl.WeatherServiceOnDemand;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lenarsharipov.weather_api.util.TestObjectUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("On Demand Weather Service")
@ExtendWith(MockitoExtension.class)
class WeatherServiceOnDemandTest {

    @Mock
    private WeatherHttpClient weatherHttpClient;

    private WeatherServiceOnDemand weatherService;

    @BeforeEach
    void setUp() {
        this.weatherService = new WeatherServiceOnDemand(API_KEY, weatherHttpClient, DEFAULT_SETTINGS);
    }

    @Test
    @DisplayName("gets weather response")
    void shouldReturnWeatherResponse() throws HttpException {
        Mockito.when(weatherHttpClient.getWeather(LOCATION, API_KEY))
                .thenReturn(WEATHER_RESPONSE);

        WeatherResponse actualResponse = weatherService.getWeather(LOCATION);

        Assertions.assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo(WEATHER_RESPONSE);
    }

    @Test
    @DisplayName("is shutdown")
    void shouldThrowExceptionWhenItShutDownAndCalledAgain() {
        weatherService.shutdown();
        assertThrows(ServiceShutDownException.class, () -> weatherService.getWeather(LOCATION));
    }

}