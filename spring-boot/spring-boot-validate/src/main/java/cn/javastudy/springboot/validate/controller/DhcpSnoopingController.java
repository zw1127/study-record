package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.dhcpsnooping.DhcpSnoopIfEntry;
import cn.javastudy.springboot.validate.domain.dhcpsnooping.DhcpSnoopingGeneral;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dhcp-snooping")
public class DhcpSnoopingController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "dhcp-snooping-general")
    public String addDhcpSnoopingGeneral(@RequestBody DhcpSnoopingGeneral dhcpSnoopingGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-entry")
    @DynamicValidation(pojoName = "dhcp-snooping-if-entry")
    public String addDhcpSnoopIfEntry(@RequestBody DhcpSnoopIfEntry dhcpSnoopIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
