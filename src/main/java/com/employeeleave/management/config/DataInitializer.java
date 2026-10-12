package com.employeeleave.management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.employeeleave.management.entity.Employee;
import com.employeeleave.management.repository.EmployeeRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(EmployeeRepository employees) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Create Admin account
            if (!employees.existsByUsername("admin")) {
                employees.save(new Employee(
                    "admin",
                    encoder.encode("admin123"),
                    "System Administrator",
                    "admin@company.com",
                    "ADMIN"
                ));
            }

            // Create Employee 1
            if (!employees.existsByUsername("employee")) {
                employees.save(new Employee(
                    "employee",
                    encoder.encode("employee123"),
                    "Demo Employee",
                    "employee@company.com",
                    "EMPLOYEE"
                ));
            }

            // Create Employee 2
            if (!employees.existsByUsername("employee2")) {
                employees.save(new Employee(
                    "employee2",
                    encoder.encode("employee123"),
                    "Employee Two",
                    "employee2@company.com",
                    "EMPLOYEE"
                ));
            }

            // Create Employee 3
            if (!employees.existsByUsername("employee3")) {
                employees.save(new Employee(
                    "employee3",
                    encoder.encode("employee123"),
                    "Employee Three",
                    "employee3@company.com",
                    "EMPLOYEE"
                ));
            }
        };
    }
}