package cn.javastudy.springboot.redis.utils;

import java.util.StringJoiner;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeyUtil {

    /**
     * redis的key.
     * 形式为：
     * 表名:主键名:主键值:列名
     *
     * @param tableName     表名
     * @param majorKey      主键名
     * @param majorKeyValue 主键值
     * @param column        列名
     * @return key.
     */
    public static String getKeyWithColumn(String tableName, String majorKey, String majorKeyValue, String column) {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(tableName)
            .add(majorKey)
            .add(majorKeyValue)
            .add(column);
        return joiner.toString();
    }

    /**
     * redis的key
     * 形式为：
     * 表名:主键名:主键值
     *
     * @param tableName     表名
     * @param majorKey      主键名
     * @param majorKeyValue 主键值
     * @return redis的key
     */
    public static String getKey(String tableName, String majorKey, String majorKeyValue) {
        return new StringJoiner(":")
            .add(tableName)
            .add(majorKey)
            .add(majorKeyValue)
            .toString();
    }
}
