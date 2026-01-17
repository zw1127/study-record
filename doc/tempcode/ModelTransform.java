import java.util.List;
import javax.annotation.Nullable;

public interface ModelTransform<O extends MibModel, T, S> {

    /**
     * 将Config模型转换成Mib模型.
     *
     * @param deviceModel deviceModel
     * @return MIB模型
     */
    O transformMibModel(@Nullable T deviceModel);

    /**
     * 将从设备取到的MIB对象转换成数据库的State模型.
     *
     * @param deviceId deviceId
     * @param mibModel MIB对象
     * @return 数据库的State模型
     */
    S transformStateModel(String deviceId, O mibModel);

    /**
     * 配置模型中需要配置的字段，在MIB模型里的Field name.
     *
     * @return fieldNames
     */
    List<String> fieldNames();
}
