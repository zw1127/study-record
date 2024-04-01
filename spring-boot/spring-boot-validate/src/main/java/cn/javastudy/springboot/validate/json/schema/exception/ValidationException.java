package cn.javastudy.springboot.validate.json.schema.exception;

import com.networknt.schema.ValidationMessage;
import java.util.Set;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 3501235110914748505L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Set<ValidationMessage> validationMessages) {
        this(validationMessages.iterator().next().getMessage());
    }
}
