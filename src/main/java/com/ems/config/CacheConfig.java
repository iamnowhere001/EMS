package com.ems.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存层配置：优先使用 Redis，连接失败时自动降级到本地内存缓存（ConcurrentMap）。
 * 部署时启动 Redis 即可自动启用分布式缓存。
 */
@Configuration
public class CacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    public static final String CACHE_DICTIONARY = "dictionary";
    public static final String CACHE_STATISTICS = "employee:statistics";

    @Value("${cache.redis-enabled:true}")
    private boolean redisEnabled;

    @Value("${cache.dictionary-ttl:600}")
    private long dictionaryTtl;

    @Value("${cache.statistics-ttl:300}")
    private long statisticsTtl;

    /**
     * 主 CacheManager：Redis 可用时使用 RedisCacheManager，否则使用 ConcurrentMap 内存版。
     */
    @Bean
    @Primary
    public CacheManager cacheManager(ObjectProvider<RedisConnectionFactory> redisFactoryProvider) {
        if (!redisEnabled) {
            log.warn("[Cache] redis-enabled=false，使用本地 ConcurrentMap 缓存（仅单机）");
            return new ConcurrentMapCacheManager(CACHE_DICTIONARY, CACHE_STATISTICS);
        }
        RedisConnectionFactory factory = redisFactoryProvider.getIfAvailable();
        if (factory == null) {
            log.warn("[Cache] 未配置 RedisConnectionFactory，降级使用本地 ConcurrentMap 缓存");
            return new ConcurrentMapCacheManager(CACHE_DICTIONARY, CACHE_STATISTICS);
        }
        try {
            factory.getConnection().ping();
            log.info("[Cache] Redis 已连接，使用 RedisCacheManager");
            return buildRedisCacheManager(factory);
        } catch (Exception e) {
            log.warn("[Cache] Redis 不可用（{}），降级使用本地 ConcurrentMap 缓存", e.getMessage());
            return new ConcurrentMapCacheManager(CACHE_DICTIONARY, CACHE_STATISTICS);
        }
    }

    private RedisCacheManager buildRedisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .computePrefixWith(name -> "ems:cache:" + name + ":")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer()))
                .disableCachingNullValues();

        ConcurrentMap<String, RedisCacheConfiguration> perCache = new ConcurrentHashMap<>();
        perCache.put(CACHE_DICTIONARY, base.entryTtl(Duration.ofSeconds(dictionaryTtl)));
        perCache.put(CACHE_STATISTICS, base.entryTtl(Duration.ofSeconds(statisticsTtl)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(perCache)
                .build();
    }

    private GenericJackson2JsonRedisSerializer jsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    /**
     * RedisTemplate：供需要直接操作 Redis 的服务使用。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer keySer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valSer = jsonSerializer();
        template.setKeySerializer(keySer);
        template.setHashKeySerializer(keySer);
        template.setValueSerializer(valSer);
        template.setHashValueSerializer(valSer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存异常处理：读/写异常仅记录，不影响业务流程。
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("[Cache] GET 异常 cache={} key={} err={}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("[Cache] PUT 异常 cache={} key={} err={}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("[Cache] EVICT 异常 cache={} key={} err={}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("[Cache] CLEAR 异常 cache={} err={}", cache.getName(), e.getMessage());
            }
        };
    }
}
