package com.ems.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办提醒聚合：合同/试用期/证书等即将到期或已到期项目。
 */
public class ReminderVO {

    private List<ContractReminderItem> expiringContracts = new ArrayList<>();
    private List<ProbationReminderItem> endingProbations = new ArrayList<>();
    private int totalExpiringContracts = 0;
    private int totalEndingProbations = 0;

    public List<ContractReminderItem> getExpiringContracts() { return expiringContracts; }
    public void setExpiringContracts(List<ContractReminderItem> expiringContracts) { this.expiringContracts = expiringContracts; }

    public List<ProbationReminderItem> getEndingProbations() { return endingProbations; }
    public void setEndingProbations(List<ProbationReminderItem> endingProbations) { this.endingProbations = endingProbations; }

    public int getTotalExpiringContracts() { return totalExpiringContracts; }
    public void setTotalExpiringContracts(int totalExpiringContracts) { this.totalExpiringContracts = totalExpiringContracts; }

    public int getTotalEndingProbations() { return totalEndingProbations; }
    public void setTotalEndingProbations(int totalEndingProbations) { this.totalEndingProbations = totalEndingProbations; }

    public static class ContractReminderItem {
        private Long employeeId;
        private String employeeName;
        private String employeeNo;
        private Long contractId;
        private String contractType;
        private String contractNo;
        private String startDate;
        private String endDate;
        private Integer daysToExpire;

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getEmployeeNo() { return employeeNo; }
        public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

        public Long getContractId() { return contractId; }
        public void setContractId(Long contractId) { this.contractId = contractId; }

        public String getContractType() { return contractType; }
        public void setContractType(String contractType) { this.contractType = contractType; }

        public String getContractNo() { return contractNo; }
        public void setContractNo(String contractNo) { this.contractNo = contractNo; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public Integer getDaysToExpire() { return daysToExpire; }
        public void setDaysToExpire(Integer daysToExpire) { this.daysToExpire = daysToExpire; }
    }

    public static class ProbationReminderItem {
        private Long employeeId;
        private String employeeName;
        private String employeeNo;
        private Long probationId;
        private String startDate;
        private String endDate;
        private Integer daysToEnd;

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getEmployeeNo() { return employeeNo; }
        public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

        public Long getProbationId() { return probationId; }
        public void setProbationId(Long probationId) { this.probationId = probationId; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public Integer getDaysToEnd() { return daysToEnd; }
        public void setDaysToEnd(Integer daysToEnd) { this.daysToEnd = daysToEnd; }
    }
}
