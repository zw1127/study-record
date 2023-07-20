package cn.javastudy.springboot.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public void greetUser(String username) {
        LOG.info("hello, {}!", username);
    }
}
