package com.employeeleave.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private int annualBalance = 12;
    @Column(nullable = false)
    private int sickBalance = 6;
    @Column(nullable = false)
    private int casualBalance = 6;

    public Employee() {}
    public Employee(String username, String passwordHash, String fullName, String email, String role) {
        this.username = username; this.passwordHash = passwordHash; this.fullName = fullName;
        this.email = email; this.role = role;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { passwordHash = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getRole() { return role; }
    public void setRole(String v) { role = v; }
    public int getAnnualBalance() { return annualBalance; }
    public void setAnnualBalance(int v) { annualBalance = v; }
    public int getSickBalance() { return sickBalance; }
    public void setSickBalance(int v) { sickBalance = v; }
    public int getCasualBalance() { return casualBalance; }
    public void setCasualBalance(int v) { casualBalance = v; }
}
