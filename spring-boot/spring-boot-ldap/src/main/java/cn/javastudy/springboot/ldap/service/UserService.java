package cn.javastudy.springboot.ldap.service;

import cn.javastudy.springboot.ldap.domain.Person;
import java.util.List;

public interface UserService {

    List<Person> getAllPersons();

    Person findPersonWithDn(String dn);

    Person addPerson(Person person);
}
