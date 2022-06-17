package cn.javastudy.springboot;

import cn.javastudy.springboot.properties.UserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PropertiesApplication {

    @Autowired
    private UserProperties userProperties;

    public static void main(String[] args) {
        SpringApplication.run(PropertiesApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            System.out.println(userProperties);
        };
    }
}