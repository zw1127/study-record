
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public abstract class BeanUtils {

    static ConcurrentMap<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();
    static ConcurrentMap<Class<?>, Map<String, Field>> fieldMapCache = new ConcurrentHashMap<>();

    public static <T> Field getDeclaredField(Class<T> objectClass, String fieldName) {
        Map<String, Field> fieldMap = getFieldMap(objectClass);
        return fieldMap.get(fieldName);
    }

    /**
     * 获取ObjectClass的所有Field列表（包含父类的field）.
     *
     * @param objectClass Object Class
     * @param <T>         T
     * @return fieldName和Field的map
     */
    public static <T> Map<String, Field> getFieldMap(Class<T> objectClass) {
        Map<String, Field> fieldMap = fieldMapCache.get(objectClass);
        if (fieldMap == null) {
            Map<String, Field> map = new HashMap<>();
            declaredFields(objectClass, field -> map.put(field.getName(), field));

            fieldMapCache.putIfAbsent(objectClass, map);
            fieldMap = fieldMapCache.get(objectClass);
        }

        return fieldMap;
    }

    public static <T> void declaredFields(Class<T> objectClass, Consumer<Field> fieldConsumer) {
        Class<?> superclass = objectClass.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            declaredFields(superclass, fieldConsumer);
        }

        Field[] fields = fieldCache.get(objectClass);
        if (fields == null) {
            Field[] declaredFields = objectClass.getDeclaredFields();

            boolean allMatch = true;
            for (Field field : declaredFields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                fields = declaredFields;
            } else {
                List<Field> list = new ArrayList<>(declaredFields.length);
                for (Field field : declaredFields) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        continue;
                    }
                    list.add(field);
                }
                fields = list.toArray(new Field[0]);
            }

            fieldCache.put(objectClass, fields);
        }

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }

            fieldConsumer.accept(field);
        }
    }
}
