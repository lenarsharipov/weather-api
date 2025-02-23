package com.lenarsharipov.weather_api.datastructure;

import com.lenarsharipov.weather_api.model.Clouds;
import com.lenarsharipov.weather_api.model.Coord;
import com.lenarsharipov.weather_api.model.Sys;
import com.lenarsharipov.weather_api.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Cache")
class CacheTest {

    private Cache cache;
    private WeatherResponse response1;
    private WeatherResponse response2;
    private static final String location = "Saint Petersburg";

    @BeforeEach
    void setUp() {
        this.cache = new Cache();
        this.response1 = createWeatherResponse(1740143220);
        this.response2 = createWeatherResponse(1740146111);
    }

    private WeatherResponse createWeatherResponse(long dt) {
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
                .name(location)
                .cod(200)
                .build();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("is initialized")
        void shouldInitializeEmptyCache() {
            assertThat(cache).isNotNull();
            assertThat(cache.size()).isZero();
        }
    }

    @Nested
    @DisplayName("Basic Operations Tests")
    class BasicOperationsTests {

        @Test
        @DisplayName("puts and gets a value")
        void shouldPutAndGetWeatherResponse() {
            cache.put(location, response1);

            assertAll(
                    () -> assertThat(cache.get(location)).isEqualTo(response1),
                    () -> assertThat(cache.size()).isOne()
            );
        }

        @Test
        @DisplayName("replaces existing entry with the same key")
        void shouldReplaceExistingEntryWithSameKey() {
            cache.put("city1", response1);
            cache.put("city1", response2);

            assertAll(
                    () -> assertThat(cache.get("city1")).isEqualTo(response2),
                    () -> assertThat(cache.size()).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("returns null for non-existent key")
        void shouldReturnNullForNonExistentKey() {
            assertNull(cache.get("non-existent"));
        }
    }

    @Nested
    @DisplayName("Cache Eviction Tests")
    class CacheEvictionTests {

        @Test
        @DisplayName("removes the oldest entry when cache is full")
        void shouldRemoveOldestEntryWhenCacheIsFull() {
            for (int i = 1; i <= 10; i++) {
                cache.put("city" + i, createWeatherResponse(i));
            }

            cache.put("city11", response1);

            assertNull(cache.get("city1"));
            assertThat(cache.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("does not remove entries when cache is not full")
        void shouldNotRemoveEntriesWhenCacheIsNotFull() {
            cache.put("city1", response1);
            cache.put("city2", response2);

            assertAll(
                    () -> assertThat(cache.get("city1")).isEqualTo(response1),
                    () -> assertThat(cache.get("city2")).isEqualTo(response2),
                    () -> assertThat(cache.size()).isEqualTo(2)
            );
        }
    }

    @Nested
    @DisplayName("Location Normalization Tests")
    class LocationNormalizationTests {

        @Test
        @DisplayName("normalizes location keys")
        void shouldNormalizeLocationKeys() {
            cache.put("  New York  ", response1);
            cache.put("los angeles", response2);

            assertAll(
                    () -> assertThat(cache.get("NEW YORK")).isEqualTo(response1),
                    () -> assertThat(cache.get("LOS ANGELES")).isEqualTo(response2),
                    () -> assertThat(cache.getLocations()).containsExactlyInAnyOrder("NEW YORK", "LOS ANGELES")
            );
        }
    }

    @Nested
    @DisplayName("Concurrency Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("is thread-safe")
        void shouldBeThreadSafe() throws InterruptedException {
            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                executorService.submit(() -> {
                    cache.put("city" + index, response1);
                    cache.get("city" + index);
                    cache.getLocations();
                });
            }

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.SECONDS);

            assertThat(cache.size()).isEqualTo(threadCount);
        }
    }

    @Nested
    @DisplayName("Clear Tests")
    class ClearTests {

        @Test
        @DisplayName("clears all entries")
        void shouldClearAllEntries() {
            cache.put("city1", response1);
            cache.put("city2", response2);
            cache.clear();

            assertAll(
                    () -> assertThat(cache.size()).isZero(),
                    () -> assertThat(cache.getLocations()).isEmpty()
            );
        }
    }
}