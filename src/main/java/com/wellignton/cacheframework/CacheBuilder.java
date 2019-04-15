package com.wellignton.cacheframework;

import java.time.ZoneId;
import java.time.ZonedDateTime;


public class CacheBuilder {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private int maxEntries = DEFAULT_INITIAL_CAPACITY;

    private ZonedDateTime duration;
    private long seconds;

    private CacheBuilder() {}
    public static CacheBuilder newBuilder() {
        return new CacheBuilder();
    }

    public Cache build() {
        return new Cache(this);
    }

    public CacheBuilder withMaxSize(int maxEntries) {
        if (maxEntries  <= 0) {
            throw new IllegalArgumentException("Size cannot be less than 1");
        }
        this.maxEntries = maxEntries;
        return this;
    }

    public CacheBuilder expireAfterWriteSeconds(long seconds) {
        this.seconds = seconds;
        this.duration = ZonedDateTime.now(ZoneId.systemDefault()).plusSeconds(seconds);
        return this;
    }

    // Getters

    public int getMaxEntries() {
        return maxEntries;
    }

    public ZonedDateTime getDuration() {
        return duration;
    }

    public long getSeconds() {
        return seconds;
    }
}
