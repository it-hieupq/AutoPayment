package com.demo.autopayment.business.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.demo.autopayment.business.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void set(String key, T value) {
        log.info("Setting value in Redis with key: {}", key);
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            String jsonValue = objectMapper.writeValueAsString(value);
            bucket.set(jsonValue);
        } catch (Exception e) {
            log.error("Error serializing object to JSON for key {}: {}", key, e.getMessage(), e);
        }
    }

    @Override
    public <T> void set(String key, T value, Duration ttl) {
        log.info("Setting value in Redis with key: {} and TTL: {}", key, ttl);
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            String jsonValue = objectMapper.writeValueAsString(value);
            bucket.set(jsonValue, ttl);
        } catch (Exception e) {
            log.error("Error serializing object to JSON for key {} with TTL {}: {}", key, ttl, e.getMessage(), e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        log.info("Getting value from Redis with key: {} and class: {}", key, clazz.getName());
        RBucket<String> bucket = redissonClient.getBucket(key);
        String raw = bucket.get();
        if (raw == null) {
            log.warn("No value found for key: {}", key);
            return null;
        }
        try {
            return objectMapper.readValue(raw, clazz);
        } catch (Exception e) {
            log.error("Error converting JSON string to object of class {} for key {}: {}", clazz.getName(), key, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        T value = get(key, clazz);
        return value != null ? value : defaultValue;
    }

    @Override
    public void remove(String key) {
        log.info("Removing key from Redis: {}", key);
        redissonClient.getBucket(key).delete();
    }

    @Override
    public <T> T get(String key, TypeReference<T> typeRef) {
        log.info("Getting value from Redis with key: {} and typeRef: {}", key, typeRef.getType());
        RBucket<String> bucket = redissonClient.getBucket(key);
        String raw = bucket.get();
        if (raw == null) {
            log.warn("No value found for key: {}", key);
            return null;
        }
        try {
            return objectMapper.readValue(raw, typeRef);
        } catch (Exception e) {
            log.error("Error converting JSON string to object of type {} for key {}: {}", typeRef.getType(), key, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> T get(String key, TypeReference<T> typeRef, T defaultVal) {
        T value = get(key, typeRef);
        return value != null ? value : defaultVal;
    }
}
