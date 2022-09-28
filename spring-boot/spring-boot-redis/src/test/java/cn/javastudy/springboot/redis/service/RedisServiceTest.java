package cn.javastudy.springboot.redis.service;

import cn.javastudy.springboot.redis.domain.User;
import cn.javastudy.springboot.redis.utils.JsonUtils;
import cn.javastudy.springboot.redis.utils.RedisKeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RedisTestConfiguration.class,
    RedisAutoConfiguration.class
})
@ActiveProfiles("unit-test")
public class RedisServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testObj() throws Exception {
        User user = new User();
        user.setAddress("上海");
        user.setName("测试dfas");
        user.setAge(123);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String key = RedisKeyUtil.getKey(User.TABLE, "name", user.getName());
        operations.set(key, JsonUtils.writeValueAsString(user));
        User userObj = JsonUtils.readValue(operations.get(key), User.class);
        System.out.println(userObj);
    }


}
