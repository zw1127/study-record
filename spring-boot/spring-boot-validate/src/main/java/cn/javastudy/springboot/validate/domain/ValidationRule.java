package cn.javastudy.springboot.validate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationRule {

    private Long id;

    // 规则类型
    private String ruleType;

    // 需要校验的实体类名称
    private String pojoName;

    // 需要校验的字段
    private String fieldName;

    // 校验规则
    private String validationRule;

}
