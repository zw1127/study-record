package cn.javastudy.springboot.json;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JsonApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonApplication.class, args);
    }

    @GetMapping(value = "/user/json/{userId}")
    public User getUserInfo(@PathVariable("userId") String userId) {
        User user = new User("test", 18);

        user.setUserId(Long.valueOf(userId));
        user.setPassword("123456");
        user.setMemo("test-memo");

        return user;
    }

}
