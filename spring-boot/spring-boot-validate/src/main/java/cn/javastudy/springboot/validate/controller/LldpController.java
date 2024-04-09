package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.lldp.LldpGeneral;
import cn.javastudy.springboot.validate.domain.lldp.LldpIfEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lldp")
@Tag(name = "lldp-controller")
public class LldpController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "lldp-general")
    public String addIgmpSnoopGeneral(@RequestBody LldpGeneral lldpGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-entry")
    @DynamicValidation(pojoName = "lldp-if-entry")
    public String addIgmpSnoopIfEntry(@RequestBody LldpIfEntry lldpIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
