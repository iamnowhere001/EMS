package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeEducation;
import com.ems.mapper.EmployeeEducationMapper;
import com.ems.service.EmployeeEducationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeEducationServiceImpl
        extends ServiceImpl<EmployeeEducationMapper, EmployeeEducation>
        implements EmployeeEducationService {

    @Override
    public List<EmployeeEducation> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeEducation>()
                .eq(EmployeeEducation::getEmployeeId, employeeId)
                .orderByAsc(EmployeeEducation::getSortOrder)
                .orderByDesc(EmployeeEducation::getStartDate));
    }

    @Override
    @Transactional
    public boolean replaceForEmployee(Long employeeId, List<EmployeeEducation> list) {
        this.remove(new LambdaQueryWrapper<EmployeeEducation>().eq(EmployeeEducation::getEmployeeId, employeeId));
        if (list == null || list.isEmpty()) {
            return true;
        }
        int idx = 0;
        for (EmployeeEducation e : list) {
            e.setId(null);
            e.setEmployeeId(employeeId);
            e.setSortOrder(idx++);
        }
        return this.saveBatch(list);
    }
}
