package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.SalaryRecord;

import java.util.List;

public interface SalaryRecordService extends IService<SalaryRecord> {
    
    IPage<SalaryRecord> pageQuery(Long employeeId, String yearMonth, Integer status, Integer page, Integer size);
    
    List<SalaryRecord> listByEmployeeId(Long employeeId);
    
    SalaryRecord getByEmployeeAndMonth(Long employeeId, String yearMonth);
    
    boolean generateMonthlySalary(String yearMonth);
    
    boolean confirmSalary(Long id);
    
    boolean batchConfirm(List<Long> ids);
    
    boolean batchPay(List<Long> ids);
}
