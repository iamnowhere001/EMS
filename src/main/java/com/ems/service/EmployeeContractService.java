package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeContract;

import java.util.List;

public interface EmployeeContractService extends IService<EmployeeContract> {

    List<EmployeeContract> listByEmployeeId(Long employeeId);

    boolean replaceForEmployee(Long employeeId, List<EmployeeContract> list);
}
