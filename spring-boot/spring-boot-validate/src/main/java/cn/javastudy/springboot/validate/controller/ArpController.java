package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.arp.ArpEntry;
import cn.javastudy.springboot.validate.domain.arp.ArpGeneral;
import cn.javastudy.springboot.validate.domain.arp.ArpIfTrapConfig;
import cn.javastudy.springboot.validate.domain.arp.ArpTrapGeneral;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/arp")
public class ArpController {

    @PostMapping("/add-arp-entry")
    @DynamicValidation(pojoName = "arp-entry")
    public String addArpEntry(@RequestBody ArpEntry arpEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-arp-general")
    @DynamicValidation(pojoName = "arp-general")
    public String addArpGeneral(@RequestBody ArpGeneral arpGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-arp-trap-general")
    @DynamicValidation(pojoName = "arp-trap-general")
    public String addArpTrapGeneral(@RequestBody ArpTrapGeneral arpTrapGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-arp-if-trap-config")
    @DynamicValidation(pojoName = "arp-if-trap-config")
    public String addArpIfTrapConfig(@RequestBody ArpIfTrapConfig arpIfTrapConfig) {
        return "请求参数验证通过，处理成功。";
    }
}
