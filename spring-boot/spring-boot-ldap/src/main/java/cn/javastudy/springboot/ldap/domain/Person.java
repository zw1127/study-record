package cn.javastudy.springboot.ldap.domain;

import javax.naming.Name;
import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Entry(base = "uid=mike,ou=people,dc=devglan,dc=com", objectClasses = "inetOrgPerson")
@Data
public class Person {

    @Id
    private Name id;

    @DnAttribute(value = "uid", index = 3)
    private String uid;

    @Attribute(name = "cn")
    private String commonName;

    @Attribute(name = "sn")
    private String userName;

    private String userPassword;
}

