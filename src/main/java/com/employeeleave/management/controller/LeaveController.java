package com.employeeleave.management.controller;

import com.employeeleave.management.dto.LeaveRequestDto;
import com.employeeleave.management.entity.Employee;
import com.employeeleave.management.entity.LeaveRequest;
import com.employeeleave.management.repository.EmployeeRepository;
import com.employeeleave.management.repository.LeaveRequestRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api")
public class LeaveController {
    private final EmployeeRepository employees;
    private final LeaveRequestRepository leaves;
    public LeaveController(EmployeeRepository employees, LeaveRequestRepository leaves) {
        this.employees = employees; this.leaves = leaves;
    }

    @GetMapping("/leaves")
    public ResponseEntity<?> myLeaves(@RequestParam String username) {
        if (employees.findByUsername(username).isEmpty()) return ResponseEntity.badRequest().body(Map.of("message","Employee not found"));
        return ResponseEntity.ok(leaves.findByEmployeeUsernameOrderByAppliedAtDesc(username).stream().map(this::toMap).toList());
    }

    @PostMapping("/leaves")
    public ResponseEntity<?> apply(@Valid @RequestBody LeaveRequestDto dto) {
        Employee employee = employees.findByUsername(dto.getUsername()).orElse(null);
        if (employee == null || "ADMIN".equals(employee.getRole())) return ResponseEntity.badRequest().body(Map.of("message","Valid employee account required"));
        if (dto.getStartDate().isBefore(LocalDate.now())) return ResponseEntity.badRequest().body(Map.of("message","Start date cannot be in the past"));
        if (dto.getEndDate().isBefore(dto.getStartDate())) return ResponseEntity.badRequest().body(Map.of("message","End date must be on or after start date"));
        if (!List.of("ANNUAL","SICK","CASUAL").contains(dto.getLeaveType().toUpperCase())) return ResponseEntity.badRequest().body(Map.of("message","Invalid leave type"));
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(dto.getLeaveType().toUpperCase());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setReason(dto.getReason().trim());
        leave.setStatus("PENDING");
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(leaves.save(leave)));
    }

    @GetMapping("/leave-balance")
    public ResponseEntity<?> balance(@RequestParam String username) {
        Employee e = employees.findByUsername(username).orElse(null);
        if (e == null) return ResponseEntity.badRequest().body(Map.of("message","Employee not found"));
        return ResponseEntity.ok(Map.of("annual",e.getAnnualBalance(),"sick",e.getSickBalance(),"casual",e.getCasualBalance()));
    }

    @GetMapping("/admin/leaves")
    public List<Map<String,Object>> allLeaves() {
        return leaves.findAllByOrderByAppliedAtDesc().stream().map(this::toMap).toList();
    }

    @PatchMapping("/admin/leaves/{id}")
    public ResponseEntity<?> decide(@PathVariable Long id, @RequestBody Map<String,String> body) {
        LeaveRequest leave = leaves.findById(id).orElse(null);
        if (leave == null) return ResponseEntity.notFound().build();
        String status = body.getOrDefault("status", "").toUpperCase();
        if (!List.of("APPROVED","REJECTED").contains(status)) return ResponseEntity.badRequest().body(Map.of("message","Status must be APPROVED or REJECTED"));
        if (!"PENDING".equals(leave.getStatus())) return ResponseEntity.badRequest().body(Map.of("message","Only pending requests can be changed"));
        if ("APPROVED".equals(status)) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            Employee e = leave.getEmployee();
            int balance = switch (leave.getLeaveType()) {
                case "ANNUAL" -> e.getAnnualBalance();
                case "SICK" -> e.getSickBalance();
                default -> e.getCasualBalance();
            };
            if (days > balance) return ResponseEntity.badRequest().body(Map.of("message","Insufficient leave balance"));
            switch (leave.getLeaveType()) {
                case "ANNUAL" -> e.setAnnualBalance((int)(e.getAnnualBalance()-days));
                case "SICK" -> e.setSickBalance((int)(e.getSickBalance()-days));
                default -> e.setCasualBalance((int)(e.getCasualBalance()-days));
            }
            employees.save(e);
        }
        leave.setStatus(status);
        leave.setManagerComment(body.getOrDefault("comment", ""));
        return ResponseEntity.ok(toMap(leaves.save(leave)));
    }

    private Map<String,Object> toMap(LeaveRequest l) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id",l.getId()); m.put("username",l.getEmployee().getUsername());
        m.put("employeeName",l.getEmployee().getFullName()); m.put("leaveType",l.getLeaveType());
        m.put("startDate",l.getStartDate()); m.put("endDate",l.getEndDate());
        m.put("days",ChronoUnit.DAYS.between(l.getStartDate(),l.getEndDate())+1);
        m.put("reason",l.getReason()); m.put("status",l.getStatus());
        m.put("managerComment",l.getManagerComment()); m.put("appliedAt",l.getAppliedAt());
        return m;
    }
}
