package cn.javastudy.springboot.aop.log.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class SysLog implements Serializable {

    private static final long serialVersionUID = -6309732882044872298L;

    private Integer id;
    private String userName;
    private String operation;
    private Integer operateTime;
    private String method;
    private String params;
    private String ip;
    private Date createTime;

}
