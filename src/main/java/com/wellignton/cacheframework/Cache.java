package com.wellignton.cacheframework;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache extends LinkedHashMap<Key, String> implements Runnable {
    private final CacheBuilder cacheConfig;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final boolean DEFAULT_ACCESS_ORDER = false;

    Cache(CacheBuilder cacheConfig) {
        super(cacheConfig.getMaxEntries(), DEFAULT_LOAD_FACTOR, DEFAULT_ACCESS_ORDER);
        this.cacheConfig = cacheConfig;

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public String put(Key key, String value) {
        writeLock.lock();
        try {
            return super.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String get(Object key) {
        readLock.lock();
        try {
            return super.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            super.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Key, String> eldest) {
        writeLock.lock();
        try {
            return size() > cacheConfig.getMaxEntries();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void run() {
        writeLock.lock();
        try {
            if (cacheConfig.getSeconds() > 0) {
                Iterator<Map.Entry<Key, String>> iterator = this.entrySet().iterator();
                while (iterator.hasNext()) {
                    Key key = iterator.next().getKey();
                    if (ChronoUnit.SECONDS.between(key.getExpire(), ZonedDateTime.now(ZoneId.systemDefault())) >= cacheConfig.getSeconds()) {
                        iterator.remove();
                    }
                }
           }
        } finally {
            writeLock.unlock();
        }
    }
}
