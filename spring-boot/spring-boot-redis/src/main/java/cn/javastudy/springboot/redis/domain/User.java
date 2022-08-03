package cn.javastudy.springboot.redis.domain;

import lombok.Data;

@Data
public class User {

    public static final String TABLE = "t_user";

    private String name;
    private String address;
    private Integer age;
}
