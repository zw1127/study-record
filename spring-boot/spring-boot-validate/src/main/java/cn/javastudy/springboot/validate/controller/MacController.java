package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.mac.MacFlapping;
import cn.javastudy.springboot.validate.domain.mac.MacForwardEntry;
import cn.javastudy.springboot.validate.domain.mac.MacGeneral;
import cn.javastudy.springboot.validate.domain.mac.MacLearning;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mac")
@Tag(name = "mac-controller")
public class MacController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "mac-general")
    public String addMacGeneral(@RequestBody MacGeneral macGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-mac-learing")
    @DynamicValidation(pojoName = "mac-learning")
    public String addMacLearning(@RequestBody MacLearning macLearning) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-mac-forward")
    @DynamicValidation(pojoName = "mac-forward-entry")
    public String addMacForwardEntry(@RequestBody MacForwardEntry macForwardEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-mac-flapping")
    @DynamicValidation(pojoName = "mac-flapping")
    public String addMacFlapping(@RequestBody MacFlapping macFlapping) {
        return "请求参数验证通过，处理成功。";
    }
}
