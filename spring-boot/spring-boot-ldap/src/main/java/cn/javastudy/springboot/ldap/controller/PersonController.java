package cn.javastudy.springboot.ldap.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @RequestMapping("/")
    public String init(Map<String, Object> model, Principal principal) {
        model.put("title", "PUBLIC AREA");
        model.put("message", "Any user can view this page");
        model.put("username", getUserName(principal));
        model.put("userroles", getUserRoles(principal));
        return "home";
    }

    @RequestMapping("/secure")
    public String secure(Map<String, Object> model, Principal principal) {
        model.put("title", "SECURE AREA");
        model.put("message", "Only Authorised Users Can See This Page");
        model.put("username", getUserName(principal));
        model.put("userroles", getUserRoles(principal));
        return "home";
    }

    private String getUserName(Principal principal) {
        if (principal == null) {
            return "anonymous";
        } else {

            final UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
            Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                System.out.println(grantedAuthority.getAuthority());
            }
            return principal.getName();
        }
    }

    private Collection<String> getUserRoles(Principal principal) {
        if (principal == null) {
            return List.of("none");
        } else {

            Set<String> roles = new HashSet<String>();

            final UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
            Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                roles.add(grantedAuthority.getAuthority());
            }
            return roles;
        }
    }
}
