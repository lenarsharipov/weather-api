package com.lenarsharipov.weather_api.util;

import com.lenarsharipov.weather_api.model.*;
import com.lenarsharipov.weather_api.settings.Settings;

import java.util.Collections;

public final class TestObjectUtils {

    public static final String API_KEY = "apiKey";
    public static final String LOCATION = "Saint Petersburg";

    public static final Settings DEFAULT_SETTINGS = Settings.builder().build();

    public static final WeatherResponse WEATHER_RESPONSE = WeatherResponse.builder()
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

    public static WeatherResponse createWeatherResponse(long dt) {
        return WeatherResponse.builder()
                .coord(new Coord(30.2642, 59.8944))
                .base("stations")
                .weather(Collections.emptyList())
                .visibility(10000)
                .clouds(new Clouds(0))
                .dt(dt)
                .sys(Sys.builder()
                        .type(2)
                        .id(2046422)
                        .country("RU")
                        .sunrise(1740115224)
                        .sunset(1740150304)
                        .build())
                .timezone(10800)
                .id(498817)
                .name(LOCATION)
                .cod(200)
                .build();
    }

    public static final String JSON_WEATHER_RESPONSE = """
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
