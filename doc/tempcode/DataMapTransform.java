import java.util.Map;

public interface DataMapTransform<T> {

    /**
     * 将canal事件中的dataMap转换成数据库中的Config模型.
     *
     * @param dataMap dataMap
     * @return 数据库中的Config模型.
     */
    T transformConfigModel(Map<String, String> dataMap);
}
