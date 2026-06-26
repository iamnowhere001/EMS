package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeEducation;

import java.util.List;

public interface EmployeeEducationService extends IService<EmployeeEducation> {

    List<EmployeeEducation> listByEmployeeId(Long employeeId);

    boolean replaceForEmployee(Long employeeId, List<EmployeeEducation> list);
}
