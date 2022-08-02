package com.dustincode.gateway.service;

public interface RedisService {

    <T> T getData(String key, Class<T> clazz);
}
