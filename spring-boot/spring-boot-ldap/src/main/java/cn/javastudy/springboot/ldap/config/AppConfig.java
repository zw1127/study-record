package cn.javastudy.springboot.ldap.config;

import cn.javastudy.springboot.ldap.client.LdapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@PropertySource("classpath:application.yml")
@ComponentScan(basePackages = {"cn.javastudy.springboot.ldap.*"})
@Profile("default")
@EnableLdapRepositories(basePackages = "cn.javastudy.springboot.ldap.**")
public class AppConfig {

//    @Autowired
//    private Environment env;
//
//    @Bean
//    public LdapContextSource contextSource() {
//        LdapContextSource contextSource = new LdapContextSource();
//        contextSource.setUrl(env.getRequiredProperty("ldap.url"));
//        contextSource.setBase(env.getRequiredProperty("ldap.partitionSuffix"));
//        contextSource.setUserDn(env.getRequiredProperty("ldap.principal"));
//        contextSource.setPassword(env.getRequiredProperty("ldap.password"));
//        return contextSource;
//    }
//
//    @Bean
//    public LdapTemplate ldapTemplate() {
//        return new LdapTemplate(contextSource());
//    }

    @Bean
    public LdapClient ldapClient() {
        return new LdapClient();
    }

}
