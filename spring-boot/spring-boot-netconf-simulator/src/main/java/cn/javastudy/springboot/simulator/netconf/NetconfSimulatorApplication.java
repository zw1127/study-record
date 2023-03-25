package cn.javastudy.springboot.simulator.netconf;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NetconfSimulatorApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(NetconfSimulatorApplication.class, args);
    }

    @RequestMapping("/hello")
    public String helloWorld() {
        return "Hello world!" + DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z").format(ZonedDateTime.now());
    }

}