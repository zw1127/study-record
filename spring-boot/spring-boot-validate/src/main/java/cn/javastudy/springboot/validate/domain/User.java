package cn.javastudy.springboot.validate.domain;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {

    private Long userNo;

    private String userName;

    private Integer age;

    private String password;
}
