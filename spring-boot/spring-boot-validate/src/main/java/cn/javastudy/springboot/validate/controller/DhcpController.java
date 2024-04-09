package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.dhcp.DhcpGeneral;
import cn.javastudy.springboot.validate.domain.dhcp.DhcpIfEntry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dhcp")
public class DhcpController {

    @PostMapping("/add-dhcp-general")
    @DynamicValidation(pojoName = "dhcp-general")
    public String addDhcpGeneral(@RequestBody DhcpGeneral dhcpGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-if-entry")
    @DynamicValidation(pojoName = "dhcp-if-entry")
    public String addDhcpIfEntry(@RequestBody DhcpIfEntry dhcpIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
