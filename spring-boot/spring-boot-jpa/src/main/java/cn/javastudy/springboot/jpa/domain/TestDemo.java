package cn.javastudy.springboot.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "test_demo")
public class TestDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false)
    private String remark;

    @Column(nullable = false)
    private String creator;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(nullable = false)
    private String updater;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(nullable = false)
    private Boolean deleted;

}
