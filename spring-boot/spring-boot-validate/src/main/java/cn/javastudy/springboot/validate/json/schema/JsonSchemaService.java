package cn.javastudy.springboot.validate.json.schema;

import cn.javastudy.springboot.validate.utils.JsonUtils;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

@Service
public class JsonSchemaService {

    private static final Logger LOG = LoggerFactory.getLogger(JsonSchemaService.class);

    private static final String JSON_SCHEMA_ROOT_PATH = "json";
    private static final String BASE_SCHEMA = "base";

    private static final Map<String, Map<String, JsonSchema>> JSON_SCHEMA_CACHE = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadSchemaFiles();
    }

    private void loadSchemaFiles() {
        try {
            Resource resource = new ClassPathResource(JSON_SCHEMA_ROOT_PATH);
            Path path = FileSystems.getDefault().getPath(resource.getFile().getCanonicalPath());

            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
//                    Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
                    LOG.info("file:{}", file);

                    try (InputStream inputStream = Files.newInputStream(file)) {
                        JsonSchema jsonSchema = schemaFactory.getSchema(inputStream);
                        String name = file.getParent().toFile().getName();
                        JSON_SCHEMA_CACHE.compute(name, (s, schemaMap) -> {
                            if (schemaMap == null) {
                                schemaMap = new HashMap<>();
                            }

                            String fileName = file.toFile().getName();
                            int index = fileName.lastIndexOf('.');
                            if (index != -1) {
                                fileName = fileName.substring(0, index);
                            }
                            schemaMap.put(fileName, jsonSchema);

                            return schemaMap;
                        });
                    } catch (Throwable throwable) {
                        LOG.warn("resolve json schema error. file:{}", file, throwable);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
            LOG.info("init all json schema file successful.");
        } catch (IOException e) {
            LOG.warn("walk file tree, error.path:{}", JSON_SCHEMA_ROOT_PATH, e);
        }
    }

    public <T> Set<ValidationMessage> validateJsonSchema(T data, String entityName) {
        try {
            Field field = ReflectionUtils.findField(data.getClass(), "nodeId");
            Objects.requireNonNull(field);
            ReflectionUtils.makeAccessible(field);
            Object object = field.get(data);
            if (object instanceof String) {
                String nodeId = (String) object;
                String sysObjectId = fetchSysObjectIdByNodeId(nodeId);

                Map<String, JsonSchema> jsonSchemaMap =
                    Optional.ofNullable(JSON_SCHEMA_CACHE.get(sysObjectId)).orElse(JSON_SCHEMA_CACHE.get(BASE_SCHEMA));

                if (!CollectionUtils.isEmpty(jsonSchemaMap)) {
                    JsonSchema jsonSchema = jsonSchemaMap.get(entityName);

                    return jsonSchema.validate(JsonUtils.valueToTree(data));
                }
            }
        } catch (Throwable throwable) {
            LOG.warn("validate json schema error.data:{}", data, throwable);
        }

        return Collections.emptySet();
    }

    private String fetchSysObjectIdByNodeId(String nodeId) {
        return nodeId;
    }

}
