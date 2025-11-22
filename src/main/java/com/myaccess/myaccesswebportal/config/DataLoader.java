package com.myaccess.myaccesswebportal.config;

import com.myaccess.myaccesswebportal.domain.Admin;
import com.myaccess.myaccesswebportal.domain.Department;
import com.myaccess.myaccesswebportal.repository.DepartmentRepository;
import com.myaccess.myaccesswebportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadInitialData(UserRepository userRepository,
                                      DepartmentRepository departmentRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                Admin admin = new Admin("admin@example.com", "changeme");
                userRepository.save(admin);
            }

            if (departmentRepository.count() == 0) {
                Department it = new Department("IT");
                Department hr = new Department("HR");
                departmentRepository.save(it);
                departmentRepository.save(hr);
            }
        };
    }
}