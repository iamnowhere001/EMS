package com.ems.dto;

import com.ems.entity.EmployeeLeave;
import com.ems.entity.EmployeeWorkflowChange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入转调离流程申请单 - 统一入口。
 * 根据 changeType 路由到不同处理逻辑：
 *  - TRANSFER：调岗（必填 fromDepartment/toDepartment/fromPosition/toPosition）
 *  - ADJUST_SALARY：调薪（必填 fromSalary/toSalary）
 *  - CONFIRM：转正（必填 employeeId）
 *  - LEAVE：离职（必填 leave 字段）
 */
public class WorkflowRequestDTO {

    /** TRANSFER / ADJUST_SALARY / CONFIRM */
    @NotBlank
    private String changeType;

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate effectiveDate;

    // TRANSFER 字段
    private String fromDepartment;
    private String toDepartment;
    private String fromPosition;
    private String toPosition;
    private String fromRank;
    private String toRank;
    private String workLocation;

    // ADJUST_SALARY 字段
    private BigDecimal fromSalary;
    private BigDecimal toSalary;
    private BigDecimal adjustmentPercent;

    // 通用
    private String applicant;
    private LocalDate applyDate;
    private String approver;
    private LocalDate approveDate;
    private String reason;
    private String remark;

    /** LEAVE 类型专用，嵌在请求体里 */
    private EmployeeLeave leave;

    public EmployeeWorkflowChange toEntity() {
        EmployeeWorkflowChange c = new EmployeeWorkflowChange();
        c.setEmployeeId(employeeId);
        c.setChangeType(changeType);
        c.setEffectiveDate(effectiveDate);
        c.setApplicant(applicant);
        c.setApplyDate(applyDate == null ? LocalDate.now() : applyDate);
        c.setApprover(approver);
        c.setApproveDate(approveDate == null ? LocalDate.now() : approveDate);
        c.setReason(reason);
        c.setRemark(remark);
        c.setStatus(1);
        return c;
    }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getFromDepartment() { return fromDepartment; }
    public void setFromDepartment(String fromDepartment) { this.fromDepartment = fromDepartment; }

    public String getToDepartment() { return toDepartment; }
    public void setToDepartment(String toDepartment) { this.toDepartment = toDepartment; }

    public String getFromPosition() { return fromPosition; }
    public void setFromPosition(String fromPosition) { this.fromPosition = fromPosition; }

    public String getToPosition() { return toPosition; }
    public void setToPosition(String toPosition) { this.toPosition = toPosition; }

    public String getFromRank() { return fromRank; }
    public void setFromRank(String fromRank) { this.fromRank = fromRank; }

    public String getToRank() { return toRank; }
    public void setToRank(String toRank) { this.toRank = toRank; }

    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }

    public BigDecimal getFromSalary() { return fromSalary; }
    public void setFromSalary(BigDecimal fromSalary) { this.fromSalary = fromSalary; }

    public BigDecimal getToSalary() { return toSalary; }
    public void setToSalary(BigDecimal toSalary) { this.toSalary = toSalary; }

    public BigDecimal getAdjustmentPercent() { return adjustmentPercent; }
    public void setAdjustmentPercent(BigDecimal adjustmentPercent) { this.adjustmentPercent = adjustmentPercent; }

    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }

    public LocalDate getApplyDate() { return applyDate; }
    public void setApplyDate(LocalDate applyDate) { this.applyDate = applyDate; }

    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }

    public LocalDate getApproveDate() { return approveDate; }
    public void setApproveDate(LocalDate approveDate) { this.approveDate = approveDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public EmployeeLeave getLeave() { return leave; }
    public void setLeave(EmployeeLeave leave) { this.leave = leave; }
}
