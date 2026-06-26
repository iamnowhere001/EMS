package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("salary_record")
public class SalaryRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    @TableField("`year_month`")
    private String yearMonth;

    private BigDecimal baseSalary;

    private BigDecimal performanceSalary;

    private BigDecimal allowance;

    private BigDecimal subsidy;

    private BigDecimal overtimePay;

    private BigDecimal deduction;

    private BigDecimal socialSecurityPersonal;

    private BigDecimal housingFundPersonal;

    private BigDecimal tax;

    private BigDecimal actualSalary;

    /** 状态：0-草稿，1-已确认，2-已发放 */
    private Integer status;

    private String remark;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getPerformanceSalary() { return performanceSalary; }
    public void setPerformanceSalary(BigDecimal performanceSalary) { this.performanceSalary = performanceSalary; }

    public BigDecimal getAllowance() { return allowance; }
    public void setAllowance(BigDecimal allowance) { this.allowance = allowance; }

    public BigDecimal getSubsidy() { return subsidy; }
    public void setSubsidy(BigDecimal subsidy) { this.subsidy = subsidy; }

    public BigDecimal getOvertimePay() { return overtimePay; }
    public void setOvertimePay(BigDecimal overtimePay) { this.overtimePay = overtimePay; }

    public BigDecimal getDeduction() { return deduction; }
    public void setDeduction(BigDecimal deduction) { this.deduction = deduction; }

    public BigDecimal getSocialSecurityPersonal() { return socialSecurityPersonal; }
    public void setSocialSecurityPersonal(BigDecimal socialSecurityPersonal) { this.socialSecurityPersonal = socialSecurityPersonal; }

    public BigDecimal getHousingFundPersonal() { return housingFundPersonal; }
    public void setHousingFundPersonal(BigDecimal housingFundPersonal) { this.housingFundPersonal = housingFundPersonal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getActualSalary() { return actualSalary; }
    public void setActualSalary(BigDecimal actualSalary) { this.actualSalary = actualSalary; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
