package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeContract;
import com.ems.mapper.EmployeeContractMapper;
import com.ems.service.EmployeeContractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeContractServiceImpl
        extends ServiceImpl<EmployeeContractMapper, EmployeeContract>
        implements EmployeeContractService {

    @Override
    public List<EmployeeContract> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeContract>()
                .eq(EmployeeContract::getEmployeeId, employeeId)
                .orderByAsc(EmployeeContract::getSortOrder)
                .orderByDesc(EmployeeContract::getStartDate));
    }

    @Override
    @Transactional
    public boolean replaceForEmployee(Long employeeId, List<EmployeeContract> list) {
        this.remove(new LambdaQueryWrapper<EmployeeContract>().eq(EmployeeContract::getEmployeeId, employeeId));
        if (list == null || list.isEmpty()) {
            return true;
        }
        int idx = 0;
        for (EmployeeContract c : list) {
            c.setId(null);
            c.setEmployeeId(employeeId);
            c.setSortOrder(idx++);
        }
        return this.saveBatch(list);
    }
}
