package com.lenarsharipov.weather_api.validation;

import com.lenarsharipov.weather_api.exception.InvalidSettingsException;
import com.lenarsharipov.weather_api.settings.Settings;

import java.util.Objects;
import java.util.function.Predicate;

public final class SettingsValidator {

    private SettingsValidator() {
    }

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

    private static boolean isValidPolling(Settings.Polling polling) {
        return polling.initialDelay() != null
                && polling.initialDelay() >= 0
                && polling.period() != null
                && polling.period() > 0
                && polling.unit() != null;
    }
}
