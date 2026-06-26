package com.ems.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmployeeStatisticsVO {

    private Long totalCount;

    private Long activeCount;

    private Long departmentCount;

    private BigDecimal totalSalary;

    private BigDecimal avgSalary;

    private Long positionCount;

    private Long maleCount;

    private Long femaleCount;

    private List<Map<String, Object>> departmentDistribution;

    private List<Map<String, Object>> positionDistribution;

    private List<Map<String, Object>> rankDistribution;

    private List<Map<String, Object>> genderDistribution;

    private List<Map<String, Object>> ageDistribution;

    private List<Map<String, Object>> hireTrend;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Long activeCount) {
        this.activeCount = activeCount;
    }

    public Long getDepartmentCount() {
        return departmentCount;
    }

    public void setDepartmentCount(Long departmentCount) {
        this.departmentCount = departmentCount;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public BigDecimal getAvgSalary() {
        return avgSalary;
    }

    public void setAvgSalary(BigDecimal avgSalary) {
        this.avgSalary = avgSalary;
    }

    public Long getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(Long positionCount) {
        this.positionCount = positionCount;
    }

    public Long getMaleCount() {
        return maleCount;
    }

    public void setMaleCount(Long maleCount) {
        this.maleCount = maleCount;
    }

    public Long getFemaleCount() {
        return femaleCount;
    }

    public void setFemaleCount(Long femaleCount) {
        this.femaleCount = femaleCount;
    }

    public List<Map<String, Object>> getDepartmentDistribution() {
        return departmentDistribution;
    }

    public void setDepartmentDistribution(List<Map<String, Object>> departmentDistribution) {
        this.departmentDistribution = departmentDistribution;
    }

    public List<Map<String, Object>> getPositionDistribution() {
        return positionDistribution;
    }

    public void setPositionDistribution(List<Map<String, Object>> positionDistribution) {
        this.positionDistribution = positionDistribution;
    }

    public List<Map<String, Object>> getRankDistribution() {
        return rankDistribution;
    }

    public void setRankDistribution(List<Map<String, Object>> rankDistribution) {
        this.rankDistribution = rankDistribution;
    }

    public List<Map<String, Object>> getGenderDistribution() {
        return genderDistribution;
    }

    public void setGenderDistribution(List<Map<String, Object>> genderDistribution) {
        this.genderDistribution = genderDistribution;
    }

    public List<Map<String, Object>> getAgeDistribution() {
        return ageDistribution;
    }

    public void setAgeDistribution(List<Map<String, Object>> ageDistribution) {
        this.ageDistribution = ageDistribution;
    }

    public List<Map<String, Object>> getHireTrend() {
        return hireTrend;
    }

    public void setHireTrend(List<Map<String, Object>> hireTrend) {
        this.hireTrend = hireTrend;
    }
}
