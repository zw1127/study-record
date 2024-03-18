package cn.javastudy.springboot.validate.mapper;

import cn.javastudy.springboot.validate.domain.ValidationRule;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ValidationRuleMapper {

    @Select("select * from validation_rule")
    @Results(id = "testDemoResult", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "ruleType", column = "rule_type"),
        @Result(property = "pojoName", column = "pojo_name"),
        @Result(property = "fieldName", column = "field_name"),
        @Result(property = "validationRule", column = "validation_rule")
    })
    List<ValidationRule> selectAll();

//    @Select({
//        "select * from validation_rule ",
//        "where rule_type = #{ruleType} and pojo_name = #{pojoName} "
//    })
//    @Results(id = "testDemoResult", value = {
//        @Result(property = "id", column = "id"),
//        @Result(property = "ruleType", column = "rule_type"),
//        @Result(property = "pojoName", column = "pojo_name"),
//        @Result(property = "fieldName", column = "field_name"),
//        @Result(property = "validationRule", column = "validation_rule")
//    })
//    ValidationRule findByPojoName(@Param("ruleType") String ruleType,
//                                  @Param("pojoName") String pojoName);

}
