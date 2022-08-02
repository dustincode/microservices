package com.dustincode.authentication.service.impl;

import com.dustincode.authentication.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setData(String key, Object value, long timeLifeInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeLifeInSeconds, TimeUnit.SECONDS);
    }
}
