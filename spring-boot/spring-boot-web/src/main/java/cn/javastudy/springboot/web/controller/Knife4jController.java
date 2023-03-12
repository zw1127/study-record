package cn.javastudy.springboot.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试模块")
@RestController
public class Knife4jController {

    @ApiImplicitParam(name = "name", value = "名称", required = true)
    @ApiOperation(value = "Hello world!")
    @ApiOperationSupport(order = 2, author = "test")
    @GetMapping("/knife4j/hi")
    public ResponseEntity<String> hello(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok("Hi:" + name);
    }

    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", required = true),
        @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    @ApiOperation(value = "接口登录！")
    @ApiOperationSupport(order = 1, author = "栈长")
    @PostMapping("/knife4j/login")
    public ResponseEntity<String> login(@RequestParam(value = "username") String username,
                                        @RequestParam(value = "password") String password) {
        if (StringUtils.isNotBlank(username) && "123456".equals(password)) {
            return ResponseEntity.ok("登录成功:" + username);
        }
        return ResponseEntity.ok("用户名或者密码有误:" + username);
    }
}
