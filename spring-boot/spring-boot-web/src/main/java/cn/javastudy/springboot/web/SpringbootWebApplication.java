package cn.javastudy.springboot.web;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@ServletComponentScan
@SpringBootApplication
@Controller
public class SpringbootWebApplication {

    private final HttpServletRequest request;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebApplication.class);
    }

    @GetMapping(value = "/test/{content}")
    public String test(@PathVariable("content") String content) {
        request.setAttribute("content", content);
        return "test";
    }


}
