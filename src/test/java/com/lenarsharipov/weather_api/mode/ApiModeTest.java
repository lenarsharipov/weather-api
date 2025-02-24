package com.lenarsharipov.weather_api.mode;

import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.service.impl.WeatherServiceOnDemand;
import com.lenarsharipov.weather_api.service.impl.WeatherServicePolling;
import com.lenarsharipov.weather_api.settings.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiMode")
class ApiModeTest {

    @Mock
    private WeatherHttpClient httpClient;

    private final String apiKey = "test-api-key";
    private final Settings defaultSettings = Settings.builder().build();

    @Test
    @DisplayName("POLLING mode creates WeatherServicePolling instance")
    void pollingModeShouldCreatePollingService() {
        WeatherService service = ApiMode.POLLING.createWeatherService(apiKey, httpClient, defaultSettings);
        assertNotNull(service);
        assertInstanceOf(WeatherServicePolling.class, service);
    }

    @Test
    @DisplayName("ON_DEMAND mode creates WeatherServiceOnDemand instance")
    void onDemandModeShouldCreateOnDemandService() {
        WeatherService service = ApiMode.ON_DEMAND.createWeatherService(apiKey, httpClient, defaultSettings);
        assertNotNull(service);
        assertInstanceOf(WeatherServiceOnDemand.class, service);
    }
}