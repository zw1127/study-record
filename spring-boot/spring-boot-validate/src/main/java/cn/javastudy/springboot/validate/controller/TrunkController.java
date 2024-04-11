package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.trunk.EthIfTrunkEntry;
import cn.javastudy.springboot.validate.domain.trunk.EthTrunkIfEntry;
import cn.javastudy.springboot.validate.domain.trunk.TrunkIfGeneral;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trunk")
@Tag(name = "trunk-controller")
public class TrunkController {

    @PostMapping("/add-eth-if-trunk")
    @DynamicValidation(pojoName = "trunk-eth")
    public String addEthIfTrunk(@RequestBody EthIfTrunkEntry ethIfTrunkEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-eth-trunk-if")
    @DynamicValidation(pojoName = "trunk-if")
    public String addEthTrunkIf(@RequestBody EthTrunkIfEntry ethTrunkIfEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-instance")
    @DynamicValidation(pojoName = "trunk-general")
    public String addTrunkIfGeneral(@RequestBody TrunkIfGeneral trunkIfGeneral) {
        return "请求参数验证通过，处理成功。";
    }
}
