package cn.javastudy.springboot.redis.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;


    public boolean existsKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖.
     *
     * @param oldKey oldKey
     * @param newKey newKey
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名.
     *
     * @param oldKey oldKey
     * @param newKey newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(String oldKey, String newKey) {
        return Boolean.TRUE.equals(redisTemplate.renameIfAbsent(oldKey, newKey));
    }

    /**
     * 删除key.
     *
     * @param key key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key.
     *
     * @param keys keys
     */
    public void deleteKey(String... keys) {
        Set<String> kSet = Stream.of(keys).collect(Collectors.toSet());
        redisTemplate.delete(kSet);
    }

    /**
     * 删除Key的集合.
     *
     * @param keys keys
     */
    public void deleteKey(Collection<String> keys) {
        Set<String> kSet = new HashSet<>(keys);
        redisTemplate.delete(kSet);
    }

    /**
     * 设置key的生命周期.
     *
     * @param key      key
     * @param time     time
     * @param timeUnit timeUnit
     */
    public void expireKey(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期.
     *
     * @param key  key
     * @param date date
     */
    public void expireKeyAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 查询key的生命周期.
     *
     * @param key      key
     * @param timeUnit timeUnit
     * @return 周期毫秒数
     */
    public Long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效.
     *
     * @param key key
     */
    public void persistKey(String key) {
        redisTemplate.persist(key);
    }
}
