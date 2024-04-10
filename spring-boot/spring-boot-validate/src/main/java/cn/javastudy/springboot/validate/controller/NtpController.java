package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.ntp.NtpAuthEntry;
import cn.javastudy.springboot.validate.domain.ntp.NtpGeneral;
import cn.javastudy.springboot.validate.domain.ntp.NtpInstanceEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ntp")
@Tag(name = "ntp-controller")
public class NtpController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "ntp-general")
    public String addNtpGeneral(@RequestBody NtpGeneral ntpGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-auth")
    @DynamicValidation(pojoName = "ntp-auth")
    public String addNtpAuth(@RequestBody NtpAuthEntry ntpAuthEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-instance")
    @DynamicValidation(pojoName = "ntp-instance")
    public String addNtpInstance(@RequestBody NtpInstanceEntry ntpInstanceEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
