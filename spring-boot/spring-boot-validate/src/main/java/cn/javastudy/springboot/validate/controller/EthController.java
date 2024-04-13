package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.ethif.EthIfEntry;
import cn.javastudy.springboot.validate.domain.ethif.EthIfOther;
import cn.javastudy.springboot.validate.domain.ethif.EthIfStatistic;
import cn.javastudy.springboot.validate.domain.ethif.EthIfStormGeneral;
import cn.javastudy.springboot.validate.domain.ethif.EthIfStormSuppress;
import cn.javastudy.springboot.validate.domain.ethif.EthOtherGeneral;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eth")
public class EthController {

    @PostMapping("/add-eth-if")
    @DynamicValidation(pojoName = "eth-if-entry")
    public String addEthIfEntry(@RequestBody EthIfEntry ethIfEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-eth-other")
    @DynamicValidation(pojoName = "eth-other-entry")
    public String addEthIfOther(@RequestBody EthIfOther ethIfOther) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-eth-statistic")
    @DynamicValidation(pojoName = "eth-statistic")
    public String addEthIfStatistic(@RequestBody EthIfStatistic ethIfStatistic) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-eth-other-general")
    @DynamicValidation(pojoName = "eth-other-general")
    public String addEthOtherGeneral(@RequestBody EthOtherGeneral ethOtherGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-eth-storm-general")
    @DynamicValidation(pojoName = "eth-storm-general")
    public String addEthIfStormGeneral(@RequestBody EthIfStormGeneral ethIfStormGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-eth-storm-suppress")
    @DynamicValidation(pojoName = "eth-storm-suppress")
    public String addEthIfStormSuppress(@RequestBody EthIfStormSuppress ethIfStormSuppress) {
        return "请求参数验证通过，处理成功。";
    }

}
