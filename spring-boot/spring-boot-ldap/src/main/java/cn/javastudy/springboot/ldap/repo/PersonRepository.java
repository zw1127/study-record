package cn.javastudy.springboot.ldap.repo;

import cn.javastudy.springboot.ldap.domain.Person;
import javax.naming.Name;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Name> {
}
