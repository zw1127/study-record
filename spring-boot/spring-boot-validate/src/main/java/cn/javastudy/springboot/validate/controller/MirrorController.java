package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.mirror.FlowMirrorIfEntry;
import cn.javastudy.springboot.validate.domain.mirror.MirrorGroupEntry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mirror")
@Tag(name = "mirror-controller")
public class MirrorController {

    @PostMapping("/add-mirror-group")
    @DynamicValidation(pojoName = "mirror-group")
    public String addMirrorGroup(@RequestBody MirrorGroupEntry mirrorGroupEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-mirror-flow-if")
    @DynamicValidation(pojoName = "mirror-flow-if")
    public String addFlowMirrorIf(@RequestBody FlowMirrorIfEntry flowMirrorIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
