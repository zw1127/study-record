package cn.javastudy.springboot.mybatis.mapper;

import cn.javastudy.springboot.mybatis.domain.TestDemo;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TestDemoMapper {

    @Select("select * from test_demo")
    @Results(id = "testDemoResult", value = {
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

    @Delete({"delete from test_demo where id = #{id}"})
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into test_demo (name, status, type, remark, creator, updater)",
        "values (#{name}, #{status}, #{type}, #{remark}, #{creator}, #{updater})"
    })
    int insert(TestDemo testDemo);

    @Update({
        " update test_demo",
        " set name = #{name},",
        " status = #{status},",
        " type = #{type},",
        " remark = #{remark},",
        " creator = #{creator},",
        " updater = #{updater}",
        " where id = #{id}"
    })
    int updateByPrimaryKey(TestDemo record);
}
