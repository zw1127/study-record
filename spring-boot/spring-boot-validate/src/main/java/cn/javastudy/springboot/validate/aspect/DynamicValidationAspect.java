package cn.javastudy.springboot.validate.aspect;

import cn.javastudy.springboot.validate.annotation.DynamicValidation;
import cn.javastudy.springboot.validate.json.schema.JsonSchemaService;
import cn.javastudy.springboot.validate.json.schema.exception.ValidationException;
import com.networknt.schema.ValidationMessage;
import java.util.Set;
import javax.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Aspect
@Component
public class DynamicValidationAspect {

    @Resource
    private JsonSchemaService jsonSchemaService;

    @Before("@annotation(dynamicValidation)")
    public void enableValidationAfterReturning(JoinPoint joinPoint,
                                               DynamicValidation dynamicValidation) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            String pojoName = dynamicValidation.pojoName();
            Set<ValidationMessage> validationMessages = jsonSchemaService.validateJsonSchema(arg, pojoName);

            if (!CollectionUtils.isEmpty(validationMessages)) {
                throw new ValidationException(validationMessages);
            }
        }
    }
}
