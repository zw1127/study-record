package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.domain.acl.FilterEntry;
import cn.javastudy.springboot.validate.domain.acl.FilterIfEntry;
import cn.javastudy.springboot.validate.domain.acl.FilterListEntry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/acl")
public class AclController {

    @PostMapping("/add-filter-list")
    @DynamicValidation(pojoName = "acl-filter-list")
    public String addFilterList(@RequestBody FilterListEntry filterListEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-filter-entry")
    @DynamicValidation(pojoName = "acl-filter-entry")
    public String addFilterEntry(@RequestBody FilterEntry filterEntry) {
        return "请求参数验证通过，处理成功。";
    }

    @PostMapping("/add-filter-if")
    @DynamicValidation(pojoName = "acl-filter-if")
    public String addFilterIf(@RequestBody FilterIfEntry filterIfEntry) {
        return "请求参数验证通过，处理成功。";
    }
}
