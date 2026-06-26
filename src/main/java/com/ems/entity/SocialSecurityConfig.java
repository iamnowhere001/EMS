package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("social_security_config")
public class SocialSecurityConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("`year_month`")
    private String yearMonth;

    private BigDecimal pensionBase;
    private BigDecimal pensionCompanyRate;
    private BigDecimal pensionPersonalRate;

    private BigDecimal medicalBase;
    private BigDecimal medicalCompanyRate;
    private BigDecimal medicalPersonalRate;

    private BigDecimal unemploymentBase;
    private BigDecimal unemploymentCompanyRate;
    private BigDecimal unemploymentPersonalRate;

    private BigDecimal injuryBase;
    private BigDecimal injuryCompanyRate;

    private BigDecimal maternityBase;
    private BigDecimal maternityCompanyRate;

    private BigDecimal housingFundBase;
    private BigDecimal housingFundCompanyRate;
    private BigDecimal housingFundPersonalRate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public BigDecimal getPensionBase() { return pensionBase; }
    public void setPensionBase(BigDecimal pensionBase) { this.pensionBase = pensionBase; }

    public BigDecimal getPensionCompanyRate() { return pensionCompanyRate; }
    public void setPensionCompanyRate(BigDecimal pensionCompanyRate) { this.pensionCompanyRate = pensionCompanyRate; }

    public BigDecimal getPensionPersonalRate() { return pensionPersonalRate; }
    public void setPensionPersonalRate(BigDecimal pensionPersonalRate) { this.pensionPersonalRate = pensionPersonalRate; }

    public BigDecimal getMedicalBase() { return medicalBase; }
    public void setMedicalBase(BigDecimal medicalBase) { this.medicalBase = medicalBase; }

    public BigDecimal getMedicalCompanyRate() { return medicalCompanyRate; }
    public void setMedicalCompanyRate(BigDecimal medicalCompanyRate) { this.medicalCompanyRate = medicalCompanyRate; }

    public BigDecimal getMedicalPersonalRate() { return medicalPersonalRate; }
    public void setMedicalPersonalRate(BigDecimal medicalPersonalRate) { this.medicalPersonalRate = medicalPersonalRate; }

    public BigDecimal getUnemploymentBase() { return unemploymentBase; }
    public void setUnemploymentBase(BigDecimal unemploymentBase) { this.unemploymentBase = unemploymentBase; }

    public BigDecimal getUnemploymentCompanyRate() { return unemploymentCompanyRate; }
    public void setUnemploymentCompanyRate(BigDecimal unemploymentCompanyRate) { this.unemploymentCompanyRate = unemploymentCompanyRate; }

    public BigDecimal getUnemploymentPersonalRate() { return unemploymentPersonalRate; }
    public void setUnemploymentPersonalRate(BigDecimal unemploymentPersonalRate) { this.unemploymentPersonalRate = unemploymentPersonalRate; }

    public BigDecimal getInjuryBase() { return injuryBase; }
    public void setInjuryBase(BigDecimal injuryBase) { this.injuryBase = injuryBase; }

    public BigDecimal getInjuryCompanyRate() { return injuryCompanyRate; }
    public void setInjuryCompanyRate(BigDecimal injuryCompanyRate) { this.injuryCompanyRate = injuryCompanyRate; }

    public BigDecimal getMaternityBase() { return maternityBase; }
    public void setMaternityBase(BigDecimal maternityBase) { this.maternityBase = maternityBase; }

    public BigDecimal getMaternityCompanyRate() { return maternityCompanyRate; }
    public void setMaternityCompanyRate(BigDecimal maternityCompanyRate) { this.maternityCompanyRate = maternityCompanyRate; }

    public BigDecimal getHousingFundBase() { return housingFundBase; }
    public void setHousingFundBase(BigDecimal housingFundBase) { this.housingFundBase = housingFundBase; }

    public BigDecimal getHousingFundCompanyRate() { return housingFundCompanyRate; }
    public void setHousingFundCompanyRate(BigDecimal housingFundCompanyRate) { this.housingFundCompanyRate = housingFundCompanyRate; }

    public BigDecimal getHousingFundPersonalRate() { return housingFundPersonalRate; }
    public void setHousingFundPersonalRate(BigDecimal housingFundPersonalRate) { this.housingFundPersonalRate = housingFundPersonalRate; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
