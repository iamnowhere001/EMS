package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.EmployeeAttendance;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeAttendanceService extends IService<EmployeeAttendance> {
    
    IPage<EmployeeAttendance> pageQuery(Long employeeId, String yearMonth, Integer page, Integer size);
    
    List<EmployeeAttendance> listByEmployeeId(Long employeeId, String yearMonth);
    
    boolean checkIn(Long employeeId, LocalDate date);
    
    boolean checkOut(Long employeeId, LocalDate date);
    
    int batchImport(List<EmployeeAttendance> attendanceList);
}
