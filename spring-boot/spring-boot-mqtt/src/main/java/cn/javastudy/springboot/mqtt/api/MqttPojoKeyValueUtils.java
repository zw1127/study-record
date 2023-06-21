package cn.javastudy.springboot.mqtt.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public final class MqttPojoKeyValueUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MqttPojoKeyValueUtils.class);

    private static final String FIELD_SEPERATE = "&";
    private static final String VALUE_SEPERATE = "=";

    private static final String PHYSICAL_SEPERATE = "_";

    private MqttPojoKeyValueUtils() {
    }

    public static <T> String pojoToKeyValueString(T instance) {
        // 获取对象的所有字段
        Collection<Field> fields = BeanUtils.getFieldMap(instance.getClass()).values();
        if (CollectionUtils.isEmpty(fields)) {
            return "";
        }

        StringJoiner stringJoiner = new StringJoiner(FIELD_SEPERATE);
        for (Field field : fields) {
            String key;
            if (field.isAnnotationPresent(MqttPojoProperty.class)) {
                // 先查找是否包含 MqttPojoProperty 注解，有的话，则取注解的值
                MqttPojoProperty mqttPojoProperty = field.getAnnotation(MqttPojoProperty.class);
                key = mqttPojoProperty.value();
            } else {
                // 将
                key = toPhysicalColumnName(field.getName());
            }

            if (StringUtils.isEmpty(key)) {
                continue;
            }

            Object value = getValue(field, instance);
            if (value == null) {
                continue;
            }

            StringJoiner inner = new StringJoiner(VALUE_SEPERATE);
            inner.add(key);
            if (Enum.class.isAssignableFrom(value.getClass())) {
                Enum<?> enumValue = (Enum<?>) value;

                inner.add(getEnumValueOrDefault(enumValue));
            } else {
                inner.add(value.toString());
            }

            stringJoiner.add(inner.toString());
        }

        return stringJoiner.toString();
    }

    public static String getEnumValueOrDefault(Enum<?> enumValue) {
        try {
            Field field = enumValue.getClass().getField(enumValue.name());
            Object value = field.get(enumValue);

            Collection<Field> fields = BeanUtils.getFieldMap(value.getClass()).values();
            for (Field enmuField : fields) {
                if (StringUtils.equals("name", enmuField.getName())
                    || StringUtils.equals("ordinal", enmuField.getName())) {
                    continue;
                }

                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    enmuField.setAccessible(true);
                    return null;
                });

                Object fieldValue = enmuField.get(value);
                // 如果枚举有Value的话，则取枚举的Value，否则取枚举的name
                if (fieldValue != null) {
                    return fieldValue.toString();
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOG.debug("get value error.", e);
        }

        return enumValue.name();
    }

    public static String toPhysicalColumnName(String camelCase) {
        // 驼峰命名转下划线分隔
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(camelCase), PHYSICAL_SEPERATE).toLowerCase();
    }

    public static String toCamelCase(String pysicalColunmName) {
        StringBuilder builder = new StringBuilder();

        String[] splits = StringUtils.split(pysicalColunmName, PHYSICAL_SEPERATE);
        for (int i = 0; i < splits.length; i++) {
            String word = splits[i];

            if (i == 0) {
                builder.append(StringUtils.uncapitalize(word));
            } else {
                builder.append(StringUtils.capitalize(word));
            }
        }
        // 下划线分隔转驼峰命名
        return builder.toString();
    }

    private static <T> Field resolveField(String key, Class<T> clazz) {
        Collection<Field> fields = BeanUtils.getFieldMap(clazz).values();

        for (Field field : fields) {
            if (field.isAnnotationPresent(MqttPojoProperty.class)) {
                MqttPojoProperty mqttPojoProperty = field.getAnnotation(MqttPojoProperty.class);
                if (StringUtils.equals(key, mqttPojoProperty.value())) {
                    return field;
                }
            }

            String camelCase = toCamelCase(key);
            if (StringUtils.equals(camelCase, field.getName())) {
                return field;
            }
        }

        return null;
    }

    public static <T> T keyValueStringToPojo(String keyValueString, Class<T> clazz) {
        T obj = null;

        try {
            obj = clazz.getDeclaredConstructor().newInstance();

            String[] keyValuePairs = keyValueString.split(FIELD_SEPERATE);

            for (String keyValuePair : keyValuePairs) {
                String[] keyValue = keyValuePair.split(VALUE_SEPERATE);
                if (keyValue.length == 2) {
                    String fieldName = keyValue[0];
                    String fieldValue = keyValue[1];

                    Field field = resolveField(fieldName, clazz);
                    if (field == null) {
                        LOG.warn("field key:{} does not exist in:{}", fieldName, clazz);
                        return null;
                    }

                    AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                        field.setAccessible(true);
                        return null;
                    });

                    // 根据字段类型设置对应的值
                    if (field.getType() == String.class) {
                        field.set(obj, fieldValue);
                    } else if (field.getType() == Integer.class || field.getType() == int.class) {
                        field.set(obj, Integer.parseInt(fieldValue));
                    } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                        field.set(obj, Boolean.parseBoolean(fieldValue));
                    } else if (Enum.class.isAssignableFrom(field.getType())) {
                        Enum<?> enumValue = getEnumValue(field.getType().asSubclass(Enum.class), fieldValue);
                        if (enumValue == null) {
                            LOG.warn("resolve field:{} value:{} error.", field, fieldValue);
                        } else {
                            field.set(obj, enumValue);
                        }
                    }
                }
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            LOG.warn("set filed error.", e);
        }

        return obj;
    }

    public static Enum<?> getEnumValue(Class<? extends Enum> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
            .filter(e -> StringUtils.equals(getEnumValueOrDefault(e), value))
            .findFirst()
            .orElse(null);
    }

    private static <T> Object getValue(Field field, T instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("get instance:{} field:{} value failed", instance, field.getName(), e);
            }
            LOG.warn("get instance:{} field:{} value failed, error:{}", instance, field.getName(), e.getMessage());
        }

        return null;
    }
}
