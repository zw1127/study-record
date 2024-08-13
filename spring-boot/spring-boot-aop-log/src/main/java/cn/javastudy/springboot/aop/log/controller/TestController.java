package cn.javastudy.springboot.aop.log.controller;

import cn.javastudy.springboot.aop.log.annotation.Log;
import cn.javastudy.springboot.aop.log.domain.SysLog;
import cn.javastudy.springboot.aop.log.mapper.SysLogMapper;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource
    private SysLogMapper sysLogMapper;

    @Log("excute method one")
    @GetMapping("/one")
    public void methodOne(String name) {

    }

    @Log("excute method two")
    @GetMapping("/two")
    public void methodTwo() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Log("excute method three")
    @GetMapping("/three")
    public void methodThree(String name, String age) {
    }

    @GetMapping("/list")
    public List<SysLog> list() {
        return this.sysLogMapper.selectAll();
    }
}
