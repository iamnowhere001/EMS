package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeFamily;

import java.util.List;

public interface EmployeeFamilyService extends IService<EmployeeFamily> {

    List<EmployeeFamily> listByEmployeeId(Long employeeId);

    boolean replaceForEmployee(Long employeeId, List<EmployeeFamily> list);
}
