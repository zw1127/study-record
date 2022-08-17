package cn.javastudy.springboot.aop.log.mapper;

import cn.javastudy.springboot.aop.log.domain.SysLog;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysLogMapper {

    @Insert("insert into sys_log (user_name, operation, operate_time, "
        + "method, params, ip, create_time) "
        + "values(#{userName}, #{operation}, #{operateTime}, "
        + "#{method}, #{params}, #{ip}, #{createTime} )")
    int insert(SysLog sysLog);

    @Select("select * from sys_log")
    @Results(id = "sysLogResult", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "operation", column = "operation"),
        @Result(property = "operateTime", column = "operate_time"),
        @Result(property = "method", column = "method"),
        @Result(property = "params", column = "params"),
        @Result(property = "ip", column = "ip"),
        @Result(property = "createTime", column = "create_time"),
    })
    List<SysLog> selectAll();
}
