package com.lenarsharipov.weather_api;

import com.lenarsharipov.weather_api.exception.*;
import com.lenarsharipov.weather_api.mode.ApiMode;
import com.lenarsharipov.weather_api.service.WeatherService;
import com.lenarsharipov.weather_api.settings.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeatherServiceFactory")
@ExtendWith(MockitoExtension.class)
class WeatherServiceFactoryTest {

    private static final String API_KEY = "test-api-key";
    private final ApiMode apiMode = ApiMode.ON_DEMAND;

    @Nested
    @DisplayName("Service Creation")
    class ServiceCreationTests {

        @Test
        @DisplayName("creates a new service for a new apiKey")
        void shouldCreateNewServiceForNewApiKey() {
            WeatherService service = WeatherServiceFactory.getWeatherService(API_KEY, apiMode);
            assertNotNull(service);
            WeatherServiceFactory.removeWeatherService(API_KEY);
        }

        @Test
        @DisplayName("creates a service with default settings")
        void shouldCreateServiceWithDefaultSettings() {
            WeatherService service = WeatherServiceFactory.getWeatherService(API_KEY, apiMode);
            assertNotNull(service);
            WeatherServiceFactory.removeWeatherService(API_KEY);
        }

        @Test
        @DisplayName("creates a service with custom settings")
        void shouldCreateServiceWithCustomSettings() {
            Settings customSettings = Settings.builder()
                    .dataFreshnessPeriod(15)
                    .cache(new Settings.Cache(200))
                    .polling(new Settings.Polling(10, 30, TimeUnit.MINUTES))
                    .build();

            WeatherService service = WeatherServiceFactory.getWeatherService(API_KEY, apiMode, customSettings);
            assertNotNull(service);
            WeatherServiceFactory.removeWeatherService(API_KEY);
        }
    }

    @Nested
    @DisplayName("Service Removal")
    class ServiceRemovalTests {

        @Test
        @DisplayName("removes and shuts down the service")
        void shouldRemoveAndShutdownService() {
            WeatherService service = WeatherServiceFactory.getWeatherService(API_KEY, apiMode);
            WeatherServiceFactory.removeWeatherService(API_KEY);

            assertThrows(ServiceNotFoundException.class,
                    () -> WeatherServiceFactory.removeWeatherService(API_KEY));

            assertThrows(ServiceShutDownException.class, () -> service.getWeather("location"));
        }

        @Test
        @DisplayName("allows creating a new service after removing the old one")
        void shouldAllowCreatingNewServiceAfterRemovingOldOne() {
            WeatherService service1 = WeatherServiceFactory.getWeatherService(API_KEY, apiMode);
            WeatherServiceFactory.removeWeatherService(API_KEY);

            WeatherService service2 = WeatherServiceFactory.getWeatherService(API_KEY, apiMode);
            assertNotEquals(service1, service2);

            WeatherServiceFactory.removeWeatherService(API_KEY);
        }

        @Test
        @DisplayName("throws ServiceNotFoundException when removing a non-existing service")
        void shouldThrowServiceNotFoundExceptionWhenRemovingNonExistingService() {
            assertThrows(ServiceNotFoundException.class,
                    () -> WeatherServiceFactory.removeWeatherService(API_KEY));
        }
    }

    @Nested
    @DisplayName("Argument Validation")
    class ArgumentValidationTests {

        @Test
        @DisplayName("throws IllegalWeatherServiceFactoryArgsException if apiKey is null or blank")
        void shouldThrowIllegalWeatherServiceFactoryArgsExceptionWhenApiKeyIsNullOrBlank() {
            assertThrows(IllegalWeatherServiceFactoryArgsException.class,
                    () -> WeatherServiceFactory.getWeatherService(null, apiMode));

            assertThrows(IllegalWeatherServiceFactoryArgsException.class,
                    () -> WeatherServiceFactory.getWeatherService("", apiMode));

            assertThrows(IllegalWeatherServiceFactoryArgsException.class,
                    () -> WeatherServiceFactory.getWeatherService("   ", apiMode));
        }

        @Test
        @DisplayName("throws IllegalWeatherServiceFactoryArgsException if apiMode is null")
        void shouldThrowIllegalWeatherServiceFactoryArgsExceptionWhenApiModeIsNull() {
            assertThrows(IllegalWeatherServiceFactoryArgsException.class,
                    () -> WeatherServiceFactory.getWeatherService(API_KEY, null));
        }

        @Test
        @DisplayName("throws InvalidSettingsException if settings are invalid")
        void shouldThrowInvalidSettingsExceptionWhenSettingsAreInvalid() {
            Settings invalidSettings = Settings.builder()
                    .dataFreshnessPeriod(-1)
                    .cache(new Settings.Cache(0))
                    .polling(new Settings.Polling(-1, 0, null))
                    .build();

            assertThrows(InvalidSettingsException.class,
                    () -> WeatherServiceFactory.getWeatherService(API_KEY, apiMode, invalidSettings));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("throws ServiceExistsException if service for apiKey already exists")
        void shouldThrowServiceExistsExceptionIfServiceAlreadyExists() {
            WeatherService service = WeatherServiceFactory.getWeatherService(API_KEY, apiMode);
            assertThrows(ServiceExistsException.class, () -> WeatherServiceFactory.getWeatherService(API_KEY, apiMode));
            WeatherServiceFactory.removeWeatherService(API_KEY);
        }
    }
}