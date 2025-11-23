package com.myaccess.myaccesswebportal.config;

import com.myaccess.myaccesswebportal.domain.Admin;
import com.myaccess.myaccesswebportal.domain.Department;
import com.myaccess.myaccesswebportal.repository.DepartmentRepository;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadInitialData(UserRepository userRepository,
                                      DepartmentRepository departmentRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@example.com";
            String rawPassword = "admin123";

            userRepository.findByEmail(adminEmail).ifPresentOrElse(existing -> {
                // update password if needed
                existing.setPasswordHash(passwordEncoder.encode(rawPassword));
                userRepository.save(existing);
            }, () -> {
                Admin admin = new Admin(adminEmail, passwordEncoder.encode(rawPassword), true);
                userRepository.save(admin);
            });

            System.out.println("Ensured admin user: " + adminEmail + " / " + rawPassword);

            if (departmentRepository.count() == 0) {
                Department it = new Department("IT");
                Department hr = new Department("HR");
                departmentRepository.save(it);
                departmentRepository.save(hr);
            }
        };
    }
}