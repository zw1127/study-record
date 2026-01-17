import java.util.List;

public interface TableProcessorRegisterService {

    void registerTableProcessor(TableProcessor tableProcessor);

    void unRegisterTableProcessor(String tableName);

    TableProcessor getTableProcessor(String tableName);

    List<TableProcessor> registedTableProcessList();
}
