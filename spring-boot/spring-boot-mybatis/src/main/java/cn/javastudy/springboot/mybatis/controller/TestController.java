package cn.javastudy.springboot.mybatis.controller;

import cn.javastudy.springboot.mybatis.domain.TestDemo;
import cn.javastudy.springboot.mybatis.mapper.TestDemoMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class TestController {

    @Resource
    private TestDemoMapper testDemoMapper;

    @GetMapping
    public List<TestDemo> list() {
        return this.testDemoMapper.selectAll();
    }

    @PutMapping
    public Boolean update(@RequestBody TestDemo testDemo) {
        return testDemoMapper.updateByPrimaryKey(testDemo) > 0;
    }

    @PostMapping
    public Boolean insert(@RequestBody TestDemo testDemo) {
        return testDemoMapper.insert(testDemo) > 0;
    }
}
