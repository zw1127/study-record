package cn.javastudy.springboot.mybatis.controller;

import cn.javastudy.springboot.mybatis.domain.TestDemo;
import cn.javastudy.springboot.mybatis.mapper.TestDemoMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource
    private TestDemoMapper testDemoMapper;

    @GetMapping("/list")
    public List<TestDemo> list() {
        return this.testDemoMapper.selectAll();
    }
}
