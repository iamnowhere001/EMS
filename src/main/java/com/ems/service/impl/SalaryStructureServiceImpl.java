package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.SalaryStructure;
import com.ems.mapper.SalaryStructureMapper;
import com.ems.service.SalaryStructureService;
import org.springframework.stereotype.Service;

@Service
public class SalaryStructureServiceImpl extends ServiceImpl<SalaryStructureMapper, SalaryStructure> implements SalaryStructureService {

    @Override
    public SalaryStructure getByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<SalaryStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalaryStructure::getEmployeeId, employeeId);
        return this.getOne(wrapper);
    }

    @Override
    public boolean saveOrUpdate(Long employeeId, SalaryStructure structure) {
        SalaryStructure existing = getByEmployeeId(employeeId);
        if (existing != null) {
            structure.setId(existing.getId());
            return this.updateById(structure);
        } else {
            structure.setEmployeeId(employeeId);
            return this.save(structure);
        }
    }
}
