package cn.javastudy.springboot.aspect;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/hello")
    public String helloWorld() {
        String time = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z").format(ZonedDateTime.now());
        return "Hello world!" + time;
    }

    @GetMapping("/greet/{user}")
    public String greetUser(@PathVariable("user") String user) {
        return userService.greetUser(user);
    }
}
