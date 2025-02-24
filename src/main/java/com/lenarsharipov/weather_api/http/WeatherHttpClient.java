package com.lenarsharipov.weather_api.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.weather_api.exception.HttpException;
import com.lenarsharipov.weather_api.model.WeatherResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WeatherHttpClient {

    private static final String ACCEPT_HEADER = "Accept";
    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiUrl;

    public WeatherHttpClient(HttpClient httpClient,
                             ObjectMapper objectMapper,
                             String apiUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiUrl = apiUrl;
    }

    /**
     * Fetches weather data for the specified location using the provided API key.
     *
     * @param location the city name or location to fetch weather data for
     * @param apiKey   the API key for authentication
     * @return the weather response
     * @throws HttpException if an error occurs during the request or response parsing
     */
    public WeatherResponse getWeather(String location,
                                      String apiKey) throws HttpException {
        HttpRequest request = buildGetRequest(location, apiKey);
        HttpResponse<String> response = sendRequest(request);
        return parseResponse(response);
    }

    /**
     * Sends an HTTP request and returns the response.
     *
     * @param request the HTTP request to send
     * @return the HTTP response
     * @throws HttpException if a network error occurs
     */
    private HttpResponse<String> sendRequest(HttpRequest request) throws HttpException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new HttpException("Network error: " + e.getMessage(), 0);
        }
    }

    /**
     * Parses the HTTP response into a WeatherResponse object.
     *
     * @param response the HTTP response to parse
     * @return the parsed WeatherResponse
     * @throws HttpException if the response cannot be parsed or contains an error
     */
    private WeatherResponse parseResponse(HttpResponse<String> response) throws HttpException {
        if (response.statusCode() == 200) {
            try {
                return objectMapper.readValue(response.body(), WeatherResponse.class);
            } catch (JsonProcessingException e) {
                throw new HttpException("Failed to parse weather data", 500);
            }
        } else {
            throw new HttpException("API error: " + response.body(), response.statusCode());
        }
    }

    /**
     * Builds an HTTP GET request for the specified location and API key.
     *
     * @param location the city name or location
     * @param apiKey   the API key for authentication
     * @return the constructed HttpRequest
     */
    private HttpRequest buildGetRequest(String location, String apiKey) {
        String url = buildUrl(location, apiKey);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header(ACCEPT_HEADER, APPLICATION_JSON)
                .build();
    }

    /**
     * Builds the URL for the HTTP request.
     *
     * @param location the city name or location
     * @param apiKey   the API key for authentication
     * @return the constructed URL
     */
    private String buildUrl(String location, String apiKey) {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        return String.format("%s?q=%s&appid=%s", apiUrl, encodedLocation, apiKey);
    }
}
