package cn.javastudy.springboot.controller;

import cn.javastudy.springboot.properties.UserProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

    private final UserProperties userProperties;

    public PropertyController(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    @GetMapping("list")
    public String list() {
        return userProperties.toString();
    }
}
