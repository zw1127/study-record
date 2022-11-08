package cn.javastudy.springboot.ldap.service.impl;

import cn.javastudy.springboot.ldap.domain.Person;
import cn.javastudy.springboot.ldap.repo.PersonRepository;
import cn.javastudy.springboot.ldap.service.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private PersonRepository personRepository;

    @Override
    public List<Person> getAllPersons() {
        List<Person> result = new ArrayList<>();

        personRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    @Override
    public Person findPersonWithDn(String dn) {
        Name dnName = LdapUtils.newLdapName(dn);

        return personRepository.findById(dnName).orElse(null);
    }

    @Override
    public Person addPerson(Person person) {
        return personRepository.save(person);
    }
}
