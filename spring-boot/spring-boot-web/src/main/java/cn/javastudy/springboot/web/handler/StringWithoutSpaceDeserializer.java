package cn.javastudy.springboot.web.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class StringWithoutSpaceDeserializer extends StdDeserializer<String> {

    private static final long serialVersionUID = -6972065572263950443L;

    public StringWithoutSpaceDeserializer(Class<String> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        return StringUtils.trimToEmpty(p.getText());
    }

}