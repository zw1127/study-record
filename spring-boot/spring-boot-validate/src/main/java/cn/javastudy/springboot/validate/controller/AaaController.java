package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.aaa.AaaGeneral;
import cn.javastudy.springboot.validate.domain.aaa.AaaTacacsServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aaa")
public class AaaController {

    @PostMapping("/add-aaa-general")
    @DynamicValidation(pojoName = "aaa-general")
    public String addAaaGeneral(@RequestBody AaaGeneral aaaGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-aaa-server")
    @DynamicValidation(pojoName = "aaa-server")
    public String addAaaServer(@RequestBody AaaTacacsServer aaaTacacsServer) {
        return "请求参数验证通过，处理成功。";
    }
}
