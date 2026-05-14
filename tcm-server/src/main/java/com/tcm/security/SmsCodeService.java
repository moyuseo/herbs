package com.tcm.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SmsCodeService {

    private static final String CODE_PREFIX = "sms:code:";
    private static final long CODE_EXPIRE_MINUTES = 5;
    private static final String MVP_CODE = "123456";

    private final RedisTemplate<String, Object> redisTemplate;

    public SmsCodeService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendCode(String phone) {
        String code = MVP_CODE;
        String key = CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        Object stored = redisTemplate.opsForValue().get(key);
        if (stored != null && stored.toString().equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
