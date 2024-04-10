package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.loopcheck.LoopcheckGeneral;
import cn.javastudy.springboot.validate.domain.loopcheck.LoopcheckIfEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loopcheck")
@Tag(name = "loopcheck-controller")
public class LoopcheckController {

    @PostMapping("/add-general")
    @DynamicValidation(pojoName = "loopcheck-general")
    public String addLoopcheckGeneral(@RequestBody LoopcheckGeneral loopcheckGeneral) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-entry")
    @DynamicValidation(pojoName = "loopcheck-if-entry")
    public String addLoopcheckIfEntry(@RequestBody LoopcheckIfEntry loopcheckIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
