package com.ems.vo;

import java.util.List;
import java.util.Map;

/**
 * Dashboard 聚合数据：6 个维度图表 + 顶部 KPI
 */
public class DashboardVO {

    private Kpi kpi;
    private List<NameValueItem> departmentDistribution;
    private List<NameValueItem> ageDistribution;
    private List<NameValueItem> tenureDistribution;
    private List<NameValueItem> educationDistribution;
    private List<NameValueItem> salaryDistribution;
    private List<NameValueItem> genderRatio;
    private List<MonthlyTrendItem> monthlyTrend;
    private List<DepartmentSalaryItem> departmentSalary;
    private List<RankDistributionItem> rankDistribution;

    public Kpi getKpi() { return kpi; }
    public void setKpi(Kpi kpi) { this.kpi = kpi; }

    public List<NameValueItem> getDepartmentDistribution() { return departmentDistribution; }
    public void setDepartmentDistribution(List<NameValueItem> departmentDistribution) { this.departmentDistribution = departmentDistribution; }

    public List<NameValueItem> getAgeDistribution() { return ageDistribution; }
    public void setAgeDistribution(List<NameValueItem> ageDistribution) { this.ageDistribution = ageDistribution; }

    public List<NameValueItem> getTenureDistribution() { return tenureDistribution; }
    public void setTenureDistribution(List<NameValueItem> tenureDistribution) { this.tenureDistribution = tenureDistribution; }

    public List<NameValueItem> getEducationDistribution() { return educationDistribution; }
    public void setEducationDistribution(List<NameValueItem> educationDistribution) { this.educationDistribution = educationDistribution; }

    public List<NameValueItem> getSalaryDistribution() { return salaryDistribution; }
    public void setSalaryDistribution(List<NameValueItem> salaryDistribution) { this.salaryDistribution = salaryDistribution; }

    public List<NameValueItem> getGenderRatio() { return genderRatio; }
    public void setGenderRatio(List<NameValueItem> genderRatio) { this.genderRatio = genderRatio; }

    public List<MonthlyTrendItem> getMonthlyTrend() { return monthlyTrend; }
    public void setMonthlyTrend(List<MonthlyTrendItem> monthlyTrend) { this.monthlyTrend = monthlyTrend; }

    public List<DepartmentSalaryItem> getDepartmentSalary() { return departmentSalary; }
    public void setDepartmentSalary(List<DepartmentSalaryItem> departmentSalary) { this.departmentSalary = departmentSalary; }

    public List<RankDistributionItem> getRankDistribution() { return rankDistribution; }
    public void setRankDistribution(List<RankDistributionItem> rankDistribution) { this.rankDistribution = rankDistribution; }

    public static class Kpi {
        private int totalCount;
        private int activeCount;
        private int leftCount;
        private int newHireThisMonth;
        private int leftThisMonth;
        private int departmentCount;
        private java.math.BigDecimal avgSalary;
        private java.math.BigDecimal totalSalary;
        private Double avgTenureYears;

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

        public int getActiveCount() { return activeCount; }
        public void setActiveCount(int activeCount) { this.activeCount = activeCount; }

        public int getLeftCount() { return leftCount; }
        public void setLeftCount(int leftCount) { this.leftCount = leftCount; }

        public int getNewHireThisMonth() { return newHireThisMonth; }
        public void setNewHireThisMonth(int newHireThisMonth) { this.newHireThisMonth = newHireThisMonth; }

        public int getLeftThisMonth() { return leftThisMonth; }
        public void setLeftThisMonth(int leftThisMonth) { this.leftThisMonth = leftThisMonth; }

        public int getDepartmentCount() { return departmentCount; }
        public void setDepartmentCount(int departmentCount) { this.departmentCount = departmentCount; }

        public java.math.BigDecimal getAvgSalary() { return avgSalary; }
        public void setAvgSalary(java.math.BigDecimal avgSalary) { this.avgSalary = avgSalary; }

        public java.math.BigDecimal getTotalSalary() { return totalSalary; }
        public void setTotalSalary(java.math.BigDecimal totalSalary) { this.totalSalary = totalSalary; }

        public Double getAvgTenureYears() { return avgTenureYears; }
        public void setAvgTenureYears(Double avgTenureYears) { this.avgTenureYears = avgTenureYears; }
    }

    public static class NameValueItem {
        private String name;
        private Object value;

        public NameValueItem() {}
        public NameValueItem(String name, Object value) { this.name = name; this.value = value; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
    }

    public static class MonthlyTrendItem {
        private String month;
        private int onboardCount;
        private int leftCount;

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public int getOnboardCount() { return onboardCount; }
        public void setOnboardCount(int onboardCount) { this.onboardCount = onboardCount; }

        public int getLeftCount() { return leftCount; }
        public void setLeftCount(int leftCount) { this.leftCount = leftCount; }
    }

    public static class DepartmentSalaryItem {
        private String department;
        private int headcount;
        private java.math.BigDecimal totalSalary;
        private java.math.BigDecimal avgSalary;

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public int getHeadcount() { return headcount; }
        public void setHeadcount(int headcount) { this.headcount = headcount; }

        public java.math.BigDecimal getTotalSalary() { return totalSalary; }
        public void setTotalSalary(java.math.BigDecimal totalSalary) { this.totalSalary = totalSalary; }

        public java.math.BigDecimal getAvgSalary() { return avgSalary; }
        public void setAvgSalary(java.math.BigDecimal avgSalary) { this.avgSalary = avgSalary; }
    }

    public static class RankDistributionItem {
        private String rank;
        private int count;

        public String getRank() { return rank; }
        public void setRank(String rank) { this.rank = rank; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}
