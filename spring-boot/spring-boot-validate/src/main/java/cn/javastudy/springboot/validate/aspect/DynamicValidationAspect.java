package cn.javastudy.springboot.validate.aspect;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.service.DynamicValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicValidationAspect {

    @Autowired
    private DynamicValidator dynamicValidator;

    @Pointcut("@annotation(dynamicValidation)")
    public void enableValidationPointcut(DynamicValidation dynamicValidation) {
    }

    @AfterReturning(pointcut = "enableValidationPointcut(dynamicValidation)", returning = "result")
    public void enableValidationAfterReturning(JoinPoint joinPoint,
                                               DynamicValidation dynamicValidation, Object result) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            String pojoName = dynamicValidation.pojoName();
            dynamicValidator.validateWithRules(arg);
        }
    }
}
