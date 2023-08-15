package cn.javastudy.springboot.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public String greetUser(String username) {
        LOG.info("hello, {}!", username);

        return "hello, " + username + "!";
    }
}
