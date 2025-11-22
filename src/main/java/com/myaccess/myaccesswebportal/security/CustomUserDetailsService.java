package com.myaccess.myaccesswebportal.security;

import com.myaccess.myaccesswebportal.domain.Admin;
import com.myaccess.myaccesswebportal.domain.Employee;
import com.myaccess.myaccesswebportal.domain.Manager;
import com.myaccess.myaccesswebportal.domain.User;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User domainUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String role;
        if (domainUser instanceof Admin) {
            role = "ROLE_ADMIN";
        } else if (domainUser instanceof Manager) {
            role = "ROLE_MANAGER";
        } else if (domainUser instanceof Employee) {
            role = "ROLE_EMPLOYEE";
        } else {
            role = "ROLE_USER";
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return org.springframework.security.core.userdetails.User
                .withUsername(domainUser.getEmail())
                .password(domainUser.getPasswordHash())
                .authorities(authorities)
                .accountLocked(!domainUser.isEnabled())
                .build();
    }
}