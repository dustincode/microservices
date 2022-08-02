package com.dustincode.authentication.service;

public interface RedisService {

    void setData(String key, Object value, long timeLifeInSeconds);
}
