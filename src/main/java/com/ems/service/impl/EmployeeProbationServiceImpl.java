package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeProbation;
import com.ems.mapper.EmployeeProbationMapper;
import com.ems.service.EmployeeProbationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeProbationServiceImpl
        extends ServiceImpl<EmployeeProbationMapper, EmployeeProbation>
        implements EmployeeProbationService {

    @Override
    public List<EmployeeProbation> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeProbation>()
                .eq(EmployeeProbation::getEmployeeId, employeeId)
                .orderByDesc(EmployeeProbation::getStartDate));
    }

    @Override
    @Transactional
    public boolean replaceForEmployee(Long employeeId, List<EmployeeProbation> list) {
        this.remove(new LambdaQueryWrapper<EmployeeProbation>().eq(EmployeeProbation::getEmployeeId, employeeId));
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (EmployeeProbation p : list) {
            p.setId(null);
            p.setEmployeeId(employeeId);
        }
        return this.saveBatch(list);
    }
}
