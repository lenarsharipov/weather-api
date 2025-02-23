package com.lenarsharipov.weather_api.mode;

import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.service.WeatherServiceOnDemand;
import com.lenarsharipov.weather_api.service.WeatherServicePolling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiMode")
class ApiModeTest {

    @Mock
    private WeatherHttpClient httpClient;

    private final String apiKey = "test-api-key";

    @Test
    @DisplayName("POLLING mode creates WeatherServicePolling instance")
    void pollingModeShouldCreatePollingService() {
        WeatherService service = ApiMode.POLLING.createWeatherService(apiKey, httpClient);
        assertNotNull(service);
        assertTrue(service instanceof WeatherServicePolling);
    }

    @Test
    @DisplayName("ON_DEMAND mode creates WeatherServiceOnDemand instance")
    void onDemandModeShouldCreateOnDemandService() {
        WeatherService service = ApiMode.ON_DEMAND.createWeatherService(apiKey, httpClient);
        assertNotNull(service);
        assertTrue(service instanceof WeatherServiceOnDemand);
    }
}