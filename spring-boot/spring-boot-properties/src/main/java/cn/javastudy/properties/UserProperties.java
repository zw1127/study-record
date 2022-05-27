package cn.javastudy.properties;

import java.util.Date;
import java.util.StringJoiner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;

@ConstructorBinding
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    private String userName;

    private String gender;
    private int age;

    private String city;
    private Date dateTime;

    public UserProperties(String userName,
                          String gender,
                          int age,
                          @DefaultValue("wuhan") String city,
                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime) {
        this.userName = userName;
        this.gender = gender;
        this.age = age;
        this.city = city;
        this.dateTime = dateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserProperties.class.getSimpleName() + "[", "]")
            .add("userName='" + userName + "'")
            .add("gender='" + gender + "'")
            .add("age=" + age)
            .add("city='" + city + "'")
            .add("dateTime=" + dateTime)
            .toString();
    }

}
