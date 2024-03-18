package cn.javastudy.springboot.validate.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

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
