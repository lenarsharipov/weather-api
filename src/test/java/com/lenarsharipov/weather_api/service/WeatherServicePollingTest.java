package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import com.lenarsharipov.weather_api.service.impl.WeatherServicePolling;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static com.lenarsharipov.weather_api.service.WeatherService.DEFAULT_DATA_FRESHNESS_PERIOD;
import static com.lenarsharipov.weather_api.util.TestObjectUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Polling Weather Service")
@ExtendWith(MockitoExtension.class)
class WeatherServicePollingTest {

    @Mock
    private WeatherHttpClient weatherHttpClient;

    private WeatherServicePolling weatherService;

    @BeforeEach
    void setUp() {
        this.weatherService = new WeatherServicePolling(API_KEY, weatherHttpClient, DEFAULT_SETTINGS);
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

    @Test
    @DisplayName("returns true if data is fresh (less than 10 minutes old)")
    void shouldReturnTrueIfDataIsFresh() {
        long currentTime = Instant.now().getEpochSecond();
        long freshDataTime = currentTime - (9 * 60); // 9 минут назад

        boolean isFresh = weatherService.isDataFresh(freshDataTime, DEFAULT_DATA_FRESHNESS_PERIOD);

        assertTrue(isFresh);
    }

    @Test
    @DisplayName("returns false if data is stale (more than 10 minutes old)")
    void shouldReturnFalseIfDataIsStale() {
        long currentTime = Instant.now().getEpochSecond();
        long staleDataTime = currentTime - (11 * 60); // 11 минут назад

        boolean isFresh = weatherService.isDataFresh(staleDataTime, DEFAULT_DATA_FRESHNESS_PERIOD);

        assertFalse(isFresh);
    }

    @Test
    @DisplayName("returns true if data is from the future")
    void shouldReturnTrueIfDataIsFromTheFuture() {
        long currentTime = Instant.now().getEpochSecond();
        long futureDataTime = currentTime + (5 * 60); // 5 минут в будущем

        boolean isFresh = weatherService.isDataFresh(futureDataTime, DEFAULT_DATA_FRESHNESS_PERIOD);

        assertTrue(isFresh);
    }

    @Test
    @DisplayName("returns false if data is very old")
    void shouldReturnFalseIfDataIsVeryOld() {
        long currentTime = Instant.now().getEpochSecond();
        long veryOldDataTime = currentTime - (24 * 60 * 60); // 24 часа назад

        boolean isFresh = weatherService.isDataFresh(veryOldDataTime, DEFAULT_DATA_FRESHNESS_PERIOD);

        assertFalse(isFresh);
    }

}