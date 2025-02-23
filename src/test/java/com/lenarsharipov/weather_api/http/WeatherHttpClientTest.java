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
        weatherHttpClient = new WeatherHttpClient(HttpClient.newHttpClient(), objectMapper, wireMockServer.baseUrl() + "/data/2.5/weather");

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

        this.jsonResponse = """
                {
                    "coord": {
                        "lon": 30.2642,
                        "lat": 59.8944
                    },
                    "weather": [
                        {
                            "id": 601,
                            "main": "Snow",
                            "description": "snow",
                            "icon": "13n"
                        }
                    ],
                    "base": "stations",
                    "main": {
                        "temp": 271.52,
                        "feels_like": 265.14,
                        "temp_min": 271.23,
                        "temp_max": 271.52,
                        "pressure": 1030,
                        "humidity": 80,
                        "sea_level": 1030,
                        "grnd_level": 1027
                    },
                    "visibility": 10000,
                    "wind": {
                        "speed": 7,
                        "deg": 210
                    },
                    "snow": {
                        "1h": 0.65
                    },
                    "clouds": {
                        "all": 20
                    },
                    "dt": 1740168302,
                    "sys": {
                        "type": 2,
                        "id": 2046422,
                        "country": "RU",
                        "sunrise": 1740115224,
                        "sunset": 1740150304
                    },
                    "timezone": 10800,
                    "id": 498817,
                    "name": "Saint Petersburg",
                    "cod": 200
                }
                """;
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
