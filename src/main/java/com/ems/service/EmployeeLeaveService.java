package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeLeave;

public interface EmployeeLeaveService extends IService<EmployeeLeave> {
    EmployeeLeave getByEmployeeId(Long employeeId);
}
