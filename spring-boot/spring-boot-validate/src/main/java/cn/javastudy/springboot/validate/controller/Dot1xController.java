package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.dot1x.Dot1xAuthConfigEntry;
import cn.javastudy.springboot.validate.domain.dot1x.Dot1xGeneral;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dot1x")
@Tag(name = "dot1x-controller")
public class Dot1xController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "dot1x-general")
    public String addDot1xGeneral(@RequestBody Dot1xGeneral dot1xGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-entry")
    @DynamicValidation(pojoName = "dot1x-auth-config-entry")
    public String addDot1xAuthConfigEntry(@RequestBody Dot1xAuthConfigEntry dot1xAuthConfigEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
