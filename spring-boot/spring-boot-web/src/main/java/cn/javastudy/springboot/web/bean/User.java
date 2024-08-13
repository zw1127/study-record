package cn.javastudy.springboot.web.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    public User(String userName, Integer age) {
        this.userName = userName;
        this.age = age;
    }

    private Long id;

    @NotNull
    @JsonProperty(value = "user-name", required = true)
    @Size(min = 5, max = 10)
    private String userName;

    @NotNull
    private Integer age;

    @JsonIgnore
    private String address;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String memo;

    public static void main(String[] args) {
        Set<Integer> set1 = Sets.newHashSet(1, 2, 3, 4, 5);
        Set<Integer> set2 = Sets.newHashSet(3, 4, 5, 6, 7);

        // 求两个 Set 的差集
        Set<Integer> differenceSet = Sets.difference(set1, set2);

        System.out.println("Set1: " + set1);
        System.out.println("Set2: " + set2);
        System.out.println("Difference Set (Set1 - Set2): " + differenceSet);
    }

}