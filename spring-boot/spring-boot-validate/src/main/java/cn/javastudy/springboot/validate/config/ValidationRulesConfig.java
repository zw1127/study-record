package cn.javastudy.springboot.validate.config;

import cn.javastudy.springboot.validate.domain.ValidationRule;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "study.validation")
public class ValidationRulesConfig {

    private List<ValidationRule> rules;

}
