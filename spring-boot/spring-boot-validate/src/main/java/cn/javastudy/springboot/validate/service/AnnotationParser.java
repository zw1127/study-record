package cn.javastudy.springboot.validate.service;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationParser {

    private static final Map<Class<? extends Annotation>, String> ANNOTATION_MAP = new HashMap<>();

    static {
        ANNOTATION_MAP.put(NotNull.class, "javax.validation.constraints.NotNull");
        ANNOTATION_MAP.put(NotBlank.class, "javax.validation.constraints.NotBlank");
        ANNOTATION_MAP.put(NotEmpty.class, "javax.validation.constraints.NotEmpty");
        ANNOTATION_MAP.put(Size.class, "javax.validation.constraints.Size");
        ANNOTATION_MAP.put(Min.class, "javax.validation.constraints.Min");
        ANNOTATION_MAP.put(Max.class, "javax.validation.constraints.Max");
        ANNOTATION_MAP.put(Pattern.class, "javax.validation.constraints.Pattern");
        ANNOTATION_MAP.put(Email.class, "org.hibernate.validator.constraints.Email");
        ANNOTATION_MAP.put(Digits.class, "javax.validation.constraints.Digits");
        ANNOTATION_MAP.put(Positive.class, "javax.validation.constraints.Positive");
        ANNOTATION_MAP.put(PositiveOrZero.class, "javax.validation.constraints.PositiveOrZero");
        ANNOTATION_MAP.put(Negative.class, "javax.validation.constraints.Negative");
        ANNOTATION_MAP.put(NegativeOrZero.class, "javax.validation.constraints.NegativeOrZero");
        ANNOTATION_MAP.put(Past.class, "javax.validation.constraints.Past");
        ANNOTATION_MAP.put(Future.class, "javax.validation.constraints.Future");
    }

    public static String parseAnnotation(String annotationString) {
        try {
            String[] parts = annotationString.split("\\(");
            String annotationName = parts[0];

            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationName);
            return parseAnnotation(annotationClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String parseAnnotation(Class<? extends Annotation> annotationClass) {
        String annotationName = ANNOTATION_MAP.get(annotationClass);
        if (annotationName != null) {
            StringBuilder rule = new StringBuilder(annotationName);
            Method[] methods = annotationClass.getDeclaredMethods();
            if (methods.length > 0) {
                rule.append("(");
                for (Method method : methods) {
                    try {
                        Object value = method.getDefaultValue();
                        if (value != null) {
                            if (rule.length() > annotationName.length() + 1) {
                                rule.append(", ");
                            }
                            rule.append(method.getName()).append("=");
                            if (value instanceof String) {
                                rule.append("\"").append(value).append("\"");
                            } else {
                                rule.append(value);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                rule.append(")");
            }
            return rule.toString();
        }
        return null;
    }
}
