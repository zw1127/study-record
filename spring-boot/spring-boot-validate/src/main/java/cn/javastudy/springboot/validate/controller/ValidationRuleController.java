package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.domain.ValidationRule;
import cn.javastudy.springboot.validate.mapper.ValidationRuleMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rule")
public class ValidationRuleController {

    @Resource
    private ValidationRuleMapper validationRuleMapper;

    @GetMapping("/list")
    public List<ValidationRule> list() {
        return this.validationRuleMapper.selectAll();
    }
}
