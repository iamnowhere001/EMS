package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeWorkflowChange;

import java.util.List;

public interface EmployeeWorkflowChangeService extends IService<EmployeeWorkflowChange> {

    List<EmployeeWorkflowChange> listByEmployeeId(Long employeeId);
}
