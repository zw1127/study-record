package cn.javastudy.springboot.jpa.repo;

import cn.javastudy.springboot.jpa.domain.TestDemo;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<TestDemo, Long> {

}

