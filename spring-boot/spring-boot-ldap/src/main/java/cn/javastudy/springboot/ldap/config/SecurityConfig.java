package cn.javastudy.springboot.ldap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    UnboundIdContainer ldapContainer() throws Exception {
        UnboundIdContainer result = new UnboundIdContainer("dc=springframework,dc=org", "classpath:users.ldif");
        result.setPort(0);
        return result;
    }

    @Bean
    DefaultSpringSecurityContextSource contextSource(UnboundIdContainer container) {
        return new DefaultSpringSecurityContextSource(
            "ldap://localhost:" + container.getPort() + "/dc=springframework,dc=org");
    }

    @Bean
    BindAuthenticator authenticator(BaseLdapPathContextSource contextSource) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserDnPatterns(new String[] { "uid={0},ou=people" });
        return authenticator;
    }

    @Bean
    LdapAuthenticationProvider authenticationProvider(LdapAuthenticator authenticator) {
        return new LdapAuthenticationProvider(authenticator);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/", "/home", "/css/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("/");
        return http.build();
    }
}
