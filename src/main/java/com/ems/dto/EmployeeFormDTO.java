package com.ems.dto;

import com.ems.entity.Employee;
import com.ems.entity.EmployeeCertificate;
import com.ems.entity.EmployeeContract;
import com.ems.entity.EmployeeEducation;
import com.ems.entity.EmployeeFamily;
import com.ems.entity.EmployeeProbation;
import com.ems.entity.EmployeeWorkExperience;

import java.util.List;

/**
 * 员工新增/编辑提交载荷：基础字段 + 6 张子表。
 * 子表为全量替换语义（按 employeeId 删除后批量保存）。
 */
public class EmployeeFormDTO {

    private Employee employee;
    private List<EmployeeEducation> educations;
    private List<EmployeeWorkExperience> workExperiences;
    private List<EmployeeFamily> families;
    private List<EmployeeCertificate> certificates;
    private List<EmployeeContract> contracts;
    private List<EmployeeProbation> probations;

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public List<EmployeeEducation> getEducations() { return educations; }
    public void setEducations(List<EmployeeEducation> educations) { this.educations = educations; }

    public List<EmployeeWorkExperience> getWorkExperiences() { return workExperiences; }
    public void setWorkExperiences(List<EmployeeWorkExperience> workExperiences) { this.workExperiences = workExperiences; }

    public List<EmployeeFamily> getFamilies() { return families; }
    public void setFamilies(List<EmployeeFamily> families) { this.families = families; }

    public List<EmployeeCertificate> getCertificates() { return certificates; }
    public void setCertificates(List<EmployeeCertificate> certificates) { this.certificates = certificates; }

    public List<EmployeeContract> getContracts() { return contracts; }
    public void setContracts(List<EmployeeContract> contracts) { this.contracts = contracts; }

    public List<EmployeeProbation> getProbations() { return probations; }
    public void setProbations(List<EmployeeProbation> probations) { this.probations = probations; }
}

