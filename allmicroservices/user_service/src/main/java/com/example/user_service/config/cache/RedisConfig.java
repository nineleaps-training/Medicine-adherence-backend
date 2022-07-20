package com.example.user_service.config.cache;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {
    @Bean
    public RedisConfiguration defaultRedisConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName("6379");
        return config;
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisConfiguration defaultRedisConfig) {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().useSsl().build();

        return new LettuceConnectionFactory(defaultRedisConfig, clientConfig);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                                      .entryTtl(Duration.ofMinutes(5))
                                      .disableCachingNullValues()
                                      .serializeValuesWith(
                                          RedisSerializationContext.SerializationPair.fromSerializer(
                                              new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder.withCacheConfiguration("usercache",
                                                           RedisCacheConfiguration.defaultCacheConfig()
                                                                                  .entryTtl(Duration.ofMinutes(5)))
                                   .withCacheConfiguration("medicinecache",
                                                           RedisCacheConfiguration.defaultCacheConfig()
                                                                                  .entryTtl(Duration.ofMinutes(10)))
                                   .withCacheConfiguration("mailcache",
                                                           RedisCacheConfiguration.defaultCacheConfig()
                                                                                  .disableCachingNullValues()
                                                                                  .entryTtl(Duration.ofMinutes(20)))
                                   .withCacheConfiguration("caretakercache",
                                                           RedisCacheConfiguration.defaultCacheConfig()
                                                                                  .disableCachingNullValues()
                                                                                  .entryTtl(Duration.ofMinutes(20)))
                                   .withCacheConfiguration("medicinehistorycache",
                                                           RedisCacheConfiguration.defaultCacheConfig()
                                                                                  .disableCachingNullValues()
                                                                                  .entryTtl(Duration.ofMinutes(20)));
    }
}

