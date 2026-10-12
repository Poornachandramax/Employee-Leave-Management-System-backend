package com.employeeleave.management.controller;

import com.employeeleave.management.dto.LoginRequest;
import com.employeeleave.management.entity.Employee;
import com.employeeleave.management.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final EmployeeRepository employees;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public AuthController(EmployeeRepository employees) { this.employees = employees; }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Employee employee = employees.findByUsername(request.getUsername()).orElse(null);
        if (employee == null || !encoder.matches(request.getPassword(), employee.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
        return ResponseEntity.ok(Map.of("id", employee.getId(), "username", employee.getUsername(),
            "fullName", employee.getFullName(), "email", employee.getEmail(), "role", employee.getRole()));
    }
}
