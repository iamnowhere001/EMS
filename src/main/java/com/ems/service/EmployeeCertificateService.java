package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeCertificate;

import java.util.List;

public interface EmployeeCertificateService extends IService<EmployeeCertificate> {

    List<EmployeeCertificate> listByEmployeeId(Long employeeId);

    boolean replaceForEmployee(Long employeeId, List<EmployeeCertificate> list);
}
