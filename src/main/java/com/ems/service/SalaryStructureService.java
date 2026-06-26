package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.SalaryStructure;

public interface SalaryStructureService extends IService<SalaryStructure> {
    
    SalaryStructure getByEmployeeId(Long employeeId);
    
    boolean saveOrUpdate(Long employeeId, SalaryStructure structure);
}
