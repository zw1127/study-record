package cn.javastudy.springboot.redis.service;

import cn.javastudy.springboot.redis.config.RedisConfig;
import com.github.fppt.jedismock.RedisServer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration(proxyBeanMethods = false)
@Lazy(false) // 禁止延迟加载
@EnableConfigurationProperties(RedisProperties.class)
@AutoConfigureBefore(RedisConfig.class)
public class RedisTestConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 创建模拟的 Redis Server 服务器
     */
    @Bean
    public RedisServer redisServer(RedisProperties properties) throws IOException {
        RedisServer redisServer = new RedisServer(properties.getPort());
        // 一次执行多个单元测试时，貌似创建多个 spring 容器，导致不进行 stop。这样，就导致端口被占用，无法启动。。。
        try {
            redisServer.start();
        } catch (Exception ex) {
            LOG.warn("start local redis server error.", ex);
        }
        return redisServer;
    }
}
