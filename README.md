# Employee Leave Management System — Java Full Stack

A runnable starter project using Java 21, Spring Boot, Spring Data JPA, MySQL, and a responsive HTML/CSS/JavaScript frontend served by Spring Boot.

## Features
- Demo login for Admin and Employee roles
- Employee dashboard with leave balance and leave history
- Apply for leave with date validation and reason
- Admin dashboard to view, approve, or reject leave requests
- Search/filter requests by status
- REST API backend and MySQL persistence
- Starter data created automatically on first run

## Requirements
- JDK 21
- MySQL 8+
- Maven 3.9+ (or use the Maven wrapper if you add one)

## 1. Create the database
Open MySQL Workbench or MySQL command line and run:
```sql
CREATE DATABASE employee_leave_db;
```

## 2. Configure database credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=root
```
Set the password to your local MySQL password.

## 3. Run the application
From this project folder:
```bash
mvn spring-boot:run
```
Then open: http://localhost:8080

## Demo accounts
- Admin: `admin` / `admin123`
- Employee: `employee` / `employee123`

These are demo credentials for local learning only. Change them and add stronger security before deployment.

## Main API endpoints
- `POST /api/auth/login`
- `GET /api/leaves?username=employee`
- `POST /api/leaves`
- `GET /api/admin/leaves`
- `PATCH /api/admin/leaves/{id}` with `{"status":"APPROVED"}` or `{"status":"REJECTED"}`
- `GET /api/leave-balance?username=employee`

## Notes
The application uses `ddl-auto=update` for learning convenience. For production, use proper migrations, Spring Security, stronger validation, audit logging, and secure password/session handling.
