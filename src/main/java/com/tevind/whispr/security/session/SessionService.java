package com.tevind.whispr.security.session;

import com.tevind.whispr.exception.SessionErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class SessionService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String sessionKey = "session:token:";


    public SessionService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean sessionExistByToken(String token) {
        return redisTemplate.hasKey(sessionKey + token);
    }

    public Boolean sessionExistByKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean setSession(String key, String userId) {
        log.debug("Setting session for user: {}", userId);
        redisTemplate.opsForValue().set(key, userId, Duration.ofDays(1));

        if (Boolean.FALSE.equals(sessionExistByKey(key))) {
            log.error("Session was not set for user: {}", userId);
            throw new SessionErrorException("Session was not set for user: " + userId);
        }

        return Boolean.TRUE;
    }

    public Boolean invalidateSession(String token) {
        log.debug("Invalidating session for token: {}", token);
        return redisTemplate.delete(sessionKey + token);
    }

    public void createSession(String token, String userId) {
        log.debug("Creating session for user: {}", userId);
        log.debug("Checking for existing session");

        if (Boolean.TRUE.equals(sessionExistByToken(token))) {
            log.debug("Session for user: {} already exist, removing session", userId);

            if (Boolean.FALSE.equals(invalidateSession(token))) {
                log.error("There was an error invalidating session for user: {}", userId);
                throw new SessionErrorException("Error invalidating session");
            }

            if (Boolean.FALSE.equals(setSession(sessionKey + token, userId))) {
                log.error("Error setting session for user: {}", userId);
                throw new SessionErrorException("Error setting session");
            }

            log.debug("Session was successfully created");
        }

        if (Boolean.FALSE.equals(setSession(sessionKey + token, userId))) {
            log.error("Error setting session for user: {}", userId);
            throw new SessionErrorException("Error setting session");
        }

        log.debug("Session was successfully created");
    }
}
