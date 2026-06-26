package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeWorkflowChange;
import com.ems.mapper.EmployeeWorkflowChangeMapper;
import com.ems.service.EmployeeWorkflowChangeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeWorkflowChangeServiceImpl
        extends ServiceImpl<EmployeeWorkflowChangeMapper, EmployeeWorkflowChange>
        implements EmployeeWorkflowChangeService {

    @Override
    public List<EmployeeWorkflowChange> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeWorkflowChange>()
                .eq(EmployeeWorkflowChange::getEmployeeId, employeeId)
                .eq(EmployeeWorkflowChange::getStatus, 1)
                .orderByDesc(EmployeeWorkflowChange::getEffectiveDate));
    }
}
