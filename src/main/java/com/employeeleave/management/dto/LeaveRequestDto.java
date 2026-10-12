package com.employeeleave.management.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public class LeaveRequestDto {
    @NotBlank private String username;
    @NotBlank private String leaveType;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotBlank private String reason;
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String v) { leaveType = v; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate v) { startDate = v; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate v) { endDate = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { reason = v; }
}
