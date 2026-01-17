package cn.javastudy.springboot.netconf.client;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NetconfClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(NetconfClientApplication.class, args);
    }

    @RequestMapping("/hello")
    public String helloWorld() {
        String time = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z").format(ZonedDateTime.now());
        return "Hello world!" + time;
    }

}