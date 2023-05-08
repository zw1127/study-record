package cn.javastudy.springboot.mqtt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class User {

    @NonNull
    @JsonProperty("user-name")
    private String userName;

    private Long userId;

    @NonNull
    private Integer age;

    private String password;

    private String address;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String memo;
}
