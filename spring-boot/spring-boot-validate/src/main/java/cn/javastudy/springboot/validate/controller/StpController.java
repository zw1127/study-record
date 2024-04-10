package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.stp.StpGeneral;
import cn.javastudy.springboot.validate.domain.stp.StpIfEntry;
import cn.javastudy.springboot.validate.domain.stp.StpInstanceEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stp")
@Tag(name = "stp-controller")
public class StpController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "stp-general")
    public String addStpGeneral(@RequestBody StpGeneral stpGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-if")
    @DynamicValidation(pojoName = "stp-if")
    public String addStpIf(@RequestBody StpIfEntry stpIfEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-instance")
    @DynamicValidation(pojoName = "stp-instance")
    public String addStpInstance(@RequestBody StpInstanceEntry stpInstanceEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
