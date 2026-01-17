package com.example.limiter.configuration;

import com.example.limiter.RateLimiterService;
import com.example.limiter.impl.SlidingWindowRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hollis
 */
@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterService slidingWindowRateLimiter(RedissonClient redisson) {
        return new SlidingWindowRateLimiter(redisson);
    }
}
