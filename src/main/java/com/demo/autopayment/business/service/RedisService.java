package com.demo.autopayment.business.service;

import java.time.Duration;
import com.fasterxml.jackson.core.type.TypeReference;

public interface RedisService {
    <T> void set(String key, T value);                        // không TTL
    <T> void set(String key, T value, Duration ttl);          // có TTL
    <T> T get(String key, Class<T> clazz);                    // ánh xạ sang kiểu đơn giản: String, Integer, DTO
    <T> T get(String key, Class<T> clazz, T defaultValue);    // fallback
    void remove(String key);
    <T> T get(String key, TypeReference<T> typeRef);               // hỗ trợ kiểu phức tạp
    <T> T get(String key, TypeReference<T> typeRef, T defaultVal); // fallback
}