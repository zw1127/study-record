package cn.javastudy.springboot.validate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ValidationConfig {

    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
