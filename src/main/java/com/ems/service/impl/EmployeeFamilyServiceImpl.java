package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeFamily;
import com.ems.mapper.EmployeeFamilyMapper;
import com.ems.service.EmployeeFamilyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeFamilyServiceImpl
        extends ServiceImpl<EmployeeFamilyMapper, EmployeeFamily>
        implements EmployeeFamilyService {

    @Override
    public List<EmployeeFamily> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeFamily>()
                .eq(EmployeeFamily::getEmployeeId, employeeId)
                .orderByAsc(EmployeeFamily::getSortOrder));
    }

    @Override
    @Transactional
    public boolean replaceForEmployee(Long employeeId, List<EmployeeFamily> list) {
        this.remove(new LambdaQueryWrapper<EmployeeFamily>().eq(EmployeeFamily::getEmployeeId, employeeId));
        if (list == null || list.isEmpty()) {
            return true;
        }
        int idx = 0;
        for (EmployeeFamily e : list) {
            e.setId(null);
            e.setEmployeeId(employeeId);
            e.setSortOrder(idx++);
        }
        return this.saveBatch(list);
    }
}
