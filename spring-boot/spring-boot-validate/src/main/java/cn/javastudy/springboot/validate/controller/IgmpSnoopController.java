package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.igmpsnoop.IgmpSnoopGeneral;
import cn.javastudy.springboot.validate.domain.igmpsnoop.IgmpSnoopIfEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/igmp-snoop")
@Tag(name = "igmp-snoop-controller")
public class IgmpSnoopController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "igmp-snoop-general")
    public String addIgmpSnoopGeneral(@RequestBody IgmpSnoopGeneral igmpSnoopGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-entry")
    @DynamicValidation(pojoName = "igmp-snoop-if-entry")
    public String addIgmpSnoopIfEntry(@RequestBody IgmpSnoopIfEntry igmpSnoopIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
