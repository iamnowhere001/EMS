package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeWorkExperience;
import com.ems.mapper.EmployeeWorkExperienceMapper;
import com.ems.service.EmployeeWorkExperienceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeWorkExperienceServiceImpl
        extends ServiceImpl<EmployeeWorkExperienceMapper, EmployeeWorkExperience>
        implements EmployeeWorkExperienceService {

    @Override
    public List<EmployeeWorkExperience> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeWorkExperience>()
                .eq(EmployeeWorkExperience::getEmployeeId, employeeId)
                .orderByAsc(EmployeeWorkExperience::getSortOrder)
                .orderByDesc(EmployeeWorkExperience::getStartDate));
    }

    @Override
    @Transactional
    public boolean replaceForEmployee(Long employeeId, List<EmployeeWorkExperience> list) {
        this.remove(new LambdaQueryWrapper<EmployeeWorkExperience>().eq(EmployeeWorkExperience::getEmployeeId, employeeId));
        if (list == null || list.isEmpty()) {
            return true;
        }
        int idx = 0;
        for (EmployeeWorkExperience e : list) {
            e.setId(null);
            e.setEmployeeId(employeeId);
            e.setSortOrder(idx++);
        }
        return this.saveBatch(list);
    }
}
