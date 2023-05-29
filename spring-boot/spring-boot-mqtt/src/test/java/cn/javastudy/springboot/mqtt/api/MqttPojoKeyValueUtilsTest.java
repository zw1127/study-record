package cn.javastudy.springboot.mqtt.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class MqttPojoKeyValueUtilsTest {

    @Test
    public void testPojoToKeyValueString() {
        User user = new User();

        user.setUserNO(100);
        user.setUserName("test");
        user.setAge(30);
        user.setCanAccess(Boolean.TRUE);
        user.setSex(Sex.FEMALE);
        user.setTestMenu(TestMenu.TEST3);
        String string = MqttPojoKeyValueUtils.pojoToKeyValueString(user);

        User pojo = MqttPojoKeyValueUtils.keyValueStringToPojo(string, User.class);
        Assert.assertEquals(user, pojo);
    }

    @Data
    private static class User {

        @MqttPojoProperty("user_no")
        private Integer userNO;

        private String userName;

        private Integer age;

        private Boolean canAccess;

        private Sex sex;

        private TestMenu testMenu;
    }

    private enum Sex {
        MALE, FEMALE
    }

    @AllArgsConstructor
    public enum TestMenu {

        TEST1("test_value1"),
        TEST2("test_value2"),
        TEST3("test_value3");

        private final String typeValue;

        public String getTypeValue() {
            return typeValue;
        }
    }
}
