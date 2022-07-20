package cn.javastudy.springboot.mybatis.mapper;

import cn.javastudy.springboot.mybatis.domain.TestDemo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TestDemoMapper {

    @Select("select * from test_demo")
    @Results(id = "testDemoResult",value= {
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "status", column = "status"),
        @Result(property = "type", column = "type"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "creator", column = "creator"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updater", column = "updater"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted")
    })
    List<TestDemo> selectAll();
}
