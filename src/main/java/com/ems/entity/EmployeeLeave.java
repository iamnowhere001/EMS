package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("employee_leave")
public class EmployeeLeave {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    private String leaveType = "主动离职";

    private LocalDate applyDate;

    private LocalDate lastWorkDate;

    private LocalDate leaveDate;

    private Long handoverTo;

    private String handoverNote;

    private String reason;

    private String exitInterviewNote;

    private String applicant;

    private String approver;

    private LocalDate approveDate;

    private LocalDate socialInsuranceCutoff;

    private LocalDate housingFundCutoff;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getApplyDate() { return applyDate; }
    public void setApplyDate(LocalDate applyDate) { this.applyDate = applyDate; }

    public LocalDate getLastWorkDate() { return lastWorkDate; }
    public void setLastWorkDate(LocalDate lastWorkDate) { this.lastWorkDate = lastWorkDate; }

    public LocalDate getLeaveDate() { return leaveDate; }
    public void setLeaveDate(LocalDate leaveDate) { this.leaveDate = leaveDate; }

    public Long getHandoverTo() { return handoverTo; }
    public void setHandoverTo(Long handoverTo) { this.handoverTo = handoverTo; }

    public String getHandoverNote() { return handoverNote; }
    public void setHandoverNote(String handoverNote) { this.handoverNote = handoverNote; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getExitInterviewNote() { return exitInterviewNote; }
    public void setExitInterviewNote(String exitInterviewNote) { this.exitInterviewNote = exitInterviewNote; }

    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }

    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }

    public LocalDate getApproveDate() { return approveDate; }
    public void setApproveDate(LocalDate approveDate) { this.approveDate = approveDate; }

    public LocalDate getSocialInsuranceCutoff() { return socialInsuranceCutoff; }
    public void setSocialInsuranceCutoff(LocalDate socialInsuranceCutoff) { this.socialInsuranceCutoff = socialInsuranceCutoff; }

    public LocalDate getHousingFundCutoff() { return housingFundCutoff; }
    public void setHousingFundCutoff(LocalDate housingFundCutoff) { this.housingFundCutoff = housingFundCutoff; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
