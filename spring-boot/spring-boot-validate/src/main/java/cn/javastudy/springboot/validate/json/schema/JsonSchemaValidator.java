package cn.javastudy.springboot.validate.json.schema;

import cn.javastudy.springboot.validate.utils.JsonUtils;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.InputStream;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonSchemaValidator {

    private static final Logger LOG = LoggerFactory.getLogger(JsonSchemaValidator.class);

    public <T> boolean validateJsonSchema(String schemaPath, T data) {
        try (InputStream schemaStream = getClass().getResourceAsStream(schemaPath)) {
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);

            Set<ValidationMessage> validationResult = schema.validate(JsonUtils.valueToTree(data));

            return validationResult.isEmpty();
        } catch (Exception e) {
            LOG.info("validate error:", e);
            return false;
        }
    }
}
