package com.tevind.whispr.security.session;

import com.tevind.whispr.exception.SessionErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class SessionService {

    private final RedisTemplate<String, Session> redisTemplate;
    private static final String tokenKey = "session:token:";
    private static final String userKey = "session:user:";


    public SessionService(RedisTemplate<String, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean sessionExistByToken(String token) {
        return redisTemplate.hasKey(tokenKey + token);
    }

    public Boolean sessionExistByKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean sessionExistByUserId(String userId) {
        return redisTemplate.hasKey(userKey + userId);
    }

    public Boolean setSession(Session session) {
        log.debug("Setting session for user: {}", session.getUserId());
        String sessionByToken = tokenKey + session.getToken();
        String sessionByUserId = userKey + session.getUserId();
        redisTemplate.opsForValue().set(sessionByToken, session, Duration.ofDays(1));
        redisTemplate.opsForValue().set(sessionByUserId, session, Duration.ofDays(1));

        if (Boolean.FALSE.equals(sessionExistByKey(sessionByToken)) ||
            Boolean.FALSE.equals(sessionExistByKey(sessionByUserId))) {
            log.error("Session was not set for user: {}", session.getUserId());
            throw new SessionErrorException("Session was not set for user: " + session.getUserId());
        }

        return Boolean.TRUE;
    }

    public Boolean invalidateSession(String userId) {
        log.debug("Invalidating session for token: {}", userId);
        String sessionByUserId = userKey + userId;

        log.debug("Checking to see if session exists for user: {}", userId);
        if (sessionExistByUserId(userId)) {
            Session session = redisTemplate.opsForValue().get(sessionByUserId);
            if (session == null) {
                log.warn("There is no session for user: {}", userId);
                return Boolean.FALSE;
            }

            log.debug("Found session for user: {}", userId);
            String token = session.getToken();
            String sessionByToken = tokenKey + token;

            log.debug("Checking to see if session exist for found token from session");
            if (sessionExistByToken(token)) {

                log.debug("Attempting to invalidate sessions");
                if (Boolean.FALSE.equals(redisTemplate.delete(sessionByToken)) ||
                    Boolean.FALSE.equals(redisTemplate.delete(sessionByUserId))) {
                    log.error("There was an error invalidating sessions");
                    throw new SessionErrorException("Error invaliding sessions");
                }

                log.debug("Session invalidated successfully");
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public void createSession(Session session) {
        log.debug("Creating session for user: {}", session.getUserId());
        log.debug("Checking for existing session");

        String userId = session.getUserId().toString();

        if (Boolean.TRUE.equals(sessionExistByUserId(userId))) {
            log.debug("Session for user: {} already exist, removing session", userId);

            if (Boolean.FALSE.equals(invalidateSession(userId))) {
                log.error("There was an error invalidating session for user: {}", userId);
                throw new SessionErrorException("Error invalidating session");
            }

            if (Boolean.FALSE.equals(setSession(session))) {
                log.error("Error setting session for user: {}", userId);
                throw new SessionErrorException("Error setting session");
            }

            log.debug("Session was successfully created");
        }

        if (Boolean.FALSE.equals(setSession(session))) {
            log.error("Error setting session for user: {}", userId);
            throw new SessionErrorException("Error setting session");
        }

        log.debug("Session was successfully created");
    }
}
