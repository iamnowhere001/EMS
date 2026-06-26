package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeLeave;
import com.ems.mapper.EmployeeLeaveMapper;
import com.ems.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeLeaveServiceImpl
        extends ServiceImpl<EmployeeLeaveMapper, EmployeeLeave>
        implements EmployeeLeaveService {

    @Override
    public EmployeeLeave getByEmployeeId(Long employeeId) {
        return this.getOne(new LambdaQueryWrapper<EmployeeLeave>()
                .eq(EmployeeLeave::getEmployeeId, employeeId)
                .orderByDesc(EmployeeLeave::getLeaveDate)
                .last("LIMIT 1"));
    }
}
