package cn.javastudy.springboot.jpa.controller;

import cn.javastudy.springboot.jpa.domain.TestDemo;
import cn.javastudy.springboot.jpa.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/list")
    public Iterable<TestDemo> list() {
        return this.userRepository.findAll();
    }
}
