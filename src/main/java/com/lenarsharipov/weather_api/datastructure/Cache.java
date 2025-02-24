package com.lenarsharipov.weather_api.datastructure;

import com.lenarsharipov.weather_api.model.WeatherResponse;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory cache for storing and retrieving weather data by location.
 *
 * <p>This cache stores a limited number of entries (currently set to 10) and
 * uses a least-recently-used (LRU) strategy to evict entries when the cache
 * is full. The cache is thread-safe.
 */
public class Cache {

    public static final Integer DEFAULT_CACHE_SIZE = 10;
    private final Map<String, WeatherResponse> cache = new ConcurrentHashMap<>();

    private final Integer cacheSize;

    public Cache(Integer cacheSize) {
        this.cacheSize = cacheSize == null
                ? DEFAULT_CACHE_SIZE
                : cacheSize;
    }

    /**
     * Retrieves the weather response for the specified location.
     *
     * @param location the location to retrieve weather data for
     * @return the weather response, or null if not found
     */
    public WeatherResponse get(String location) {
        return cache.get(normalizeLocation(location));
    }

    /**
     * Stores the weather response for the specified location.
     * If the cache is full, the oldest entry is removed.
     *
     * @param location the location to store weather data for
     * @param weatherResponse the weather response to store
     */
    public void put(String location, WeatherResponse weatherResponse) {
        String normalizedKey = normalizeLocation(location);
        if (isCacheFull() && !cache.containsKey(normalizedKey)) {
            removeOldestEntry();
        }
        cache.put(normalizedKey, weatherResponse);
    }

    /**
     * Removes the oldest entry from the cache.
     */
    public void removeOldestEntry() {
        cache.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().dt()))
                .map(Map.Entry::getKey)
                .ifPresent(cache::remove);
    }

    /**
     * Returns a set of all locations currently in the cache.
     *
     * @return a set of locations
     */
    public Set<String> getLocations() {
        return cache.keySet();
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Normalizes the location key by trimming and converting to uppercase.
     *
     * @param key the location key to normalize
     * @return the normalized location key
     */
    private String normalizeLocation(String key) {
        return key.trim().toUpperCase();
    }

    /**
     * Returns the current size of the cache.
     *
     * @return the number of entries in the cache
     */
    public int size() {
        return cache.size();
    }

    /**
     * Checks if the cache is full.
     *
     * @return true if the cache is full, false otherwise
     */
    private boolean isCacheFull() {
        return cache.size() >= cacheSize;
    }
}
