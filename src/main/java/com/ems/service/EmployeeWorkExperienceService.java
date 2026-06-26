package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeWorkExperience;

import java.util.List;

public interface EmployeeWorkExperienceService extends IService<EmployeeWorkExperience> {

    List<EmployeeWorkExperience> listByEmployeeId(Long employeeId);

    boolean replaceForEmployee(Long employeeId, List<EmployeeWorkExperience> list);
}
