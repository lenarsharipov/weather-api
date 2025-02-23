package com.lenarsharipov.weather_api.validation;

import com.lenarsharipov.weather_api.exception.InvalidSettingsException;
import com.lenarsharipov.weather_api.settings.Settings;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A utility class for validating settings for the weather service.
 * <p>
 * The methods in this class validate the settings for the weather service,
 * throwing an exception if the settings are invalid.
 */
public final class SettingsValidator {

    private SettingsValidator() {
    }

    /**
     * Validates the specified settings.
     * <p>
     * The method checks that:
     * <ul>
     * <li>the settings are not null</li>
     * <li>the data freshness period is positive</li>
     * <li>the cache size is positive</li>
     * <li>the polling settings are valid (see {@link #isValidPolling(Settings.Polling)}).</li>
     * </ul>
     * If any of the conditions is not met, an exception is thrown.
     *
     * @param settings the settings to validate
     * @throws InvalidSettingsException if the settings are invalid
     */
    public static void validate(Settings settings) {
        validate(settings,
                "settings",
                Objects::nonNull,
                "Settings cannot be null");

        validate(settings.dataFreshnessPeriod(),
                "dataFreshnessPeriod",
                value -> value != null && value > 0,
                "Data freshness period must be positive");

        validate(settings.cache(),
                "cache",
                cache -> cache != null
                        && cache.size() != null
                        && cache.size() > 0,
                "Cache size must be positive");

        validate(settings.polling(),
                "polling",
                polling -> polling != null && isValidPolling(polling),
                "Polling settings are invalid");
    }

    private static <T> void validate(T value,
                                     String fieldName,
                                     Predicate<T> validator,
                                     String errorMessage) {
        if (!validator.test(value)) {
            throw new InvalidSettingsException(fieldName + ": " + errorMessage);
        }
    }

    /**
     * Checks whether the specified polling settings are valid.
     * <p>
     * The method checks that:
     * <ul>
     * <li>the initial delay is not null and is not negative</li>
     * <li>the period is not null and is positive</li>
     * <li>the unit is not null</li>
     * </ul>
     * If any of the conditions is not met, the method returns false.
     *
     * @param polling the polling settings to check
     * @return true if the polling settings are valid, false otherwise
     */
    private static boolean isValidPolling(Settings.Polling polling) {
        return polling.initialDelay() != null
                && polling.initialDelay() >= 0
                && polling.period() != null
                && polling.period() > 0
                && polling.unit() != null;
    }
}
