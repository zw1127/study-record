package cn.javastudy.springboot.validate.controller;

import cn.javastudy.springboot.validate.domain.Person;
import cn.javastudy.springboot.validate.json.schema.JsonSchemaValidator;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private JsonSchemaValidator jsonSchemaValidator;

    @PostMapping("/add")
//    @DynamicValidation(pojoName = "Person")
    public String add(@Valid @RequestBody Person person) {
        String schemaPath = "/json/person-schema.json"; // JSON Schema 文件的路径

        boolean isValid = jsonSchemaValidator.validateJsonSchema(schemaPath, person);

        if (!isValid) {
            return "请求参数验证失败：JSON 数据不符合 JSON Schema 规范。";
        }

        // 处理请求
        // your business logic here

        return "请求参数验证通过，处理成功。";
    }
}
