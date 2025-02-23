package com.lenarsharipov.weather_api.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.lenarsharipov.weather_api.util.TestObjectUtils.JSON_WEATHER_RESPONSE;
import static com.lenarsharipov.weather_api.util.TestObjectUtils.WEATHER_RESPONSE;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Http-client")
@ExtendWith(MockitoExtension.class)
class WeatherHttpClientTest {

    private static WireMockServer wireMockServer;
    private WeatherHttpClient weatherHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private WeatherResponse weatherResponse;
    private String jsonResponse;
    private final String location = "Saint Petersburg";
    private final String apiKey = "test-api-key";

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        weatherHttpClient = new WeatherHttpClient(
                HttpClient.newHttpClient(),
                objectMapper,
                wireMockServer.baseUrl() + "/data/2.5/weather");

        this.weatherResponse = WEATHER_RESPONSE;
        this.jsonResponse = JSON_WEATHER_RESPONSE;
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("gets weather")
    void getWeatherSuccess() throws Exception {

        wireMockServer.stubFor(get(urlMatching("/data/2.5/weather.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                        .withStatus(200)));

        WeatherResponse actualResponse = weatherHttpClient.getWeather(location, apiKey);

        assertNotNull(actualResponse);
        Assertions.assertThat(actualResponse).isEqualTo(weatherResponse);
    }

    @Test
    @DisplayName("gets exception on network error")
    void sendRequestShouldThrowHttpExceptionOnNetworkError() {
        wireMockServer.stubFor(get(urlMatching("/data/2.5/weather.*"))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        HttpException exception = assertThrows(HttpException.class, () ->
                weatherHttpClient.getWeather(location, apiKey));

        assertEquals("Network error: Connection reset", exception.getMessage());
        assertEquals(0, exception.getStatusCode());
    }

    @Test
    @DisplayName("gets parse exception")
    void parseResponseShouldThrowHttpExceptionOnErrorResponse() {
        wireMockServer.stubFor(get(urlMatching("/data/2.5/weather.*"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        HttpException exception = assertThrows(HttpException.class, () ->
                weatherHttpClient.getWeather(location, apiKey));

        assertEquals("API error: Internal Server Error", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    @DisplayName("gets json parse exception")
    void parseResponseShouldThrowHttpExceptionOnJsonProcessingException() {
        wireMockServer.stubFor(get(urlMatching("/data/2.5/weather.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{invalid_json}") // Некорректный JSON
                        .withStatus(200)));

        HttpException exception = assertThrows(HttpException.class, () ->
                weatherHttpClient.getWeather(location, apiKey));

        assertEquals("Failed to parse weather data", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

}
