package com.lenarsharipov.weather_api.service;

import com.lenarsharipov.weather_api.datastructure.Cache;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.exception.ServiceShutDownException;
import com.lenarsharipov.weather_api.http.WeatherHttpClient;
import com.lenarsharipov.weather_api.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("On Demand Weather Service")
@ExtendWith(MockitoExtension.class)
class WeatherServiceOnDemandTest {

    @Mock
    private WeatherHttpClient weatherHttpClient;

    @Mock
    private Cache cache;

    @InjectMocks
    private WeatherServiceOnDemand weatherService;
    private final String apiKey = "apiKey";
    private final String location = "Saint Petersburg";

    private WeatherResponse weatherResponse;

    @BeforeEach
    void setUp() {
        this.weatherService = new WeatherServiceOnDemand(apiKey, weatherHttpClient);
        this.weatherResponse = WeatherResponse.builder()
                .coord(new Coord(30.2642, 59.8944))
                .weather(Collections.singletonList(
                        new Weather(601, "Snow", "snow", "13n")))
                .base("stations")
                .main(Main.builder()
                        .temp(271.52)
                        .feelsLike(265.14)
                        .tempMin(271.23)
                        .tempMax(271.52)
                        .pressure(1030)
                        .humidity(80)
                        .seaLevel(1030)
                        .groundLevel(1027)
                        .build())
                .visibility(10000)
                .wind(new Wind(7, 210, 0.0))
                .snow(new Snow(0.65))
                .clouds(new Clouds(20))
                .dt(1740168302)
                .sys(Sys.builder()
                        .type(2)
                        .id(2046422)
                        .country("RU")
                        .sunrise(1740115224)
                        .sunset(1740150304)
                        .build())
                .timezone(10800)
                .id(498817)
                .name("Saint Petersburg")
                .cod(200)
                .build();
    }

    @Test
    @DisplayName("gets weather response")
    void shouldReturnWeatherResponse() throws HttpException {
        Mockito.when(weatherHttpClient.getWeather(location, apiKey))
                .thenReturn(weatherResponse);

        WeatherResponse actualResponse = weatherService.getWeather(location);

        Assertions.assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo(weatherResponse);
    }

    @Test
    @DisplayName("is shutdown")
    void shouldThrowExceptionWhenItShutDownAndCalledAgain() {
        weatherService.shutdown();
        assertThrows(ServiceShutDownException.class, () -> weatherService.getWeather(location));
    }

}