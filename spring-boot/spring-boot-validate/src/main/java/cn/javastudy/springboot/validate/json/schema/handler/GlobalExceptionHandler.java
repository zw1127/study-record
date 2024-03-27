package cn.javastudy.springboot.validate.json.schema.handler;

import cn.javastudy.springboot.validate.json.schema.exception.ValidationException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<String> handleException(HttpServletRequest request, Throwable ex) {
        LOG.error("global exception:", ex);
        return new ResponseEntity<>("global exception: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<String> handleMethodArgumentNotValidException(ValidationException ex) {
        String msg = "parameter validate failed:" + ex.getMessage();
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

}