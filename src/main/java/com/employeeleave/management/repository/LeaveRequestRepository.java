package com.employeeleave.management.repository;

import com.employeeleave.management.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeUsernameOrderByAppliedAtDesc(String username);
    List<LeaveRequest> findAllByOrderByAppliedAtDesc();
}
