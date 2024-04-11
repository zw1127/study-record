package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.trunk.EthIfTrunkEntry;
import cn.javastudy.springboot.validate.domain.trunk.EthTrunkIfEntry;
import cn.javastudy.springboot.validate.domain.trunk.TrunkIfGeneral;
import cn.javastudy.springboot.validate.domain.vlan.VlanEthBinding;
import cn.javastudy.springboot.validate.domain.vlan.VlanEthIf;
import cn.javastudy.springboot.validate.domain.vlan.VlanIf;
import cn.javastudy.springboot.validate.domain.vlan.VlanIp;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vlan")
@Tag(name = "vlan-controller")
public class VlanController {

    @PostMapping("/update-vlan-if")
    @DynamicValidation(pojoName = "vlan-if")
    public String updateVlan(@RequestBody VlanIf vlanIf) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-vlan-ip")
    @DynamicValidation(pojoName = "vlan-ip")
    public String addVlanIp(@RequestBody VlanIp vlanIp) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/update-eth-vlan")
    @DynamicValidation(pojoName = "vlan-eth-if")
    public String updateEthVlan(@RequestBody VlanEthIf vlanEthIf) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/eth-binding-vlan")
    @DynamicValidation(pojoName = "vlan-eth-bingding")
    public String ethBindingVlan(@RequestBody VlanEthBinding vlanEthBinding) {
        return "请求参数验证通过，处理成功。";
    }
}
