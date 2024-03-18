package cn.javastudy.springboot.validate.service;

import cn.javastudy.springboot.validate.config.ValidationRulesConfig;
import cn.javastudy.springboot.validate.domain.ValidationRule;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicValidator {

    @Autowired
    private ValidationRulesConfig validationRulesConfig;

    @Autowired
    private Validator validator;

    public void validateWithRules(Object object) throws ValidationException {
        List<ValidationRule> rules = validationRulesConfig.getRules();
        for (ValidationRule rule : rules) {
            if (rule.getPojoName().equals(object.getClass().getSimpleName())) {
                String validationRule = rule.getValidationRule();
                if (validationRule != null && !validationRule.isEmpty()) {
                    try {
                        Class<? extends Annotation> annotation = parseAsAnnotation(validationRule);
                        Set<ConstraintViolation<Object>> violations = validator.validate(object, annotation);
                        if (!violations.isEmpty()) {
                            throw new ValidationException(violations.iterator().next().getMessage());
                        }
//                        if (annotation != null) {
//                        }
                    } catch (Exception e) {
                        throw new ValidationException("Validation failed: " + e.getMessage());
                    }
                }
            }
        }
    }

    private Class<? extends Annotation> parseAsAnnotation(String validationRule) throws ValidationException {
        try {
            // Splitting the rule to separate annotation name and its attributes
            String[] parts = validationRule.split("\\(");
            String annotationName = parts[0];

            // Creating an instance of the annotation class
            return  (Class<? extends Annotation>) Class.forName(annotationName);

            // If there are attributes specified
        } catch (Exception e) {
            throw new ValidationException("Error parsing validation rule: " + validationRule, e);
        }
    }
//    private Annotation parseAsAnnotation(String validationRule) throws ValidationException {
//        try {
//            // Splitting the rule to separate annotation name and its attributes
//            String[] parts = validationRule.split("\\(");
//            String annotationName = parts[0];
//
//            // Creating an instance of the annotation class
//            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationName);
//
//            // If there are attributes specified
//            if (parts.length > 1) {
//                String[] attributes = parts[1].replaceAll("\\)$", "").split(",");
//
//                // Creating a dynamic proxy for the annotation
//                return (Annotation) Proxy.newProxyInstance(
//                    annotationClass.getClassLoader(),
//                    new Class[]{annotationClass},
//                    new AnnotationInvocationHandler(annotationClass, attributes)
//                );
//            } else {
//                // If no attributes are specified, simply return an instance of the annotation
//                return annotationClass.newInstance();
//            }
//        } catch (Exception e) {
//            throw new ValidationException("Error parsing validation rule: " + validationRule, e);
//        }
//    }

    private static class AnnotationInvocationHandler implements InvocationHandler {
        private final Class<? extends Annotation> annotationType;
        private final String[] attributes;

        public AnnotationInvocationHandler(Class<? extends Annotation> annotationType, String[] attributes) {
            this.annotationType = annotationType;
            this.attributes = attributes;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Method invoked: " + method.getName());

            if (method.getName().equals("annotationType")) {
                return annotationType;
            }

            String attributeName = method.getName();
            for (String attribute : attributes) {
                String[] keyValue = attribute.split("=");
                if (keyValue[0].equals(attributeName)) {
                    return parseAttributeValue(method.getReturnType(), keyValue[1]);
                }
            }
            throw new IllegalStateException("Attribute not found: " + attributeName);
        }

        private Object parseAttributeValue(Class<?> type, String value) {
            if (type.equals(String.class)) {
                return value;
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                return Boolean.parseBoolean(value);
            }
            throw new IllegalArgumentException("Unsupported attribute type: " + type);
        }
    }
}
