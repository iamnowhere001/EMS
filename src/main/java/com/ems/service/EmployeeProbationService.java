package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeProbation;

import java.util.List;

public interface EmployeeProbationService extends IService<EmployeeProbation> {

    List<EmployeeProbation> listByEmployeeId(Long employeeId);

    boolean replaceForEmployee(Long employeeId, List<EmployeeProbation> list);
}
