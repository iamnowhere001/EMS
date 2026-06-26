package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeCertificate;
import com.ems.mapper.EmployeeCertificateMapper;
import com.ems.service.EmployeeCertificateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeCertificateServiceImpl
        extends ServiceImpl<EmployeeCertificateMapper, EmployeeCertificate>
        implements EmployeeCertificateService {

    @Override
    public List<EmployeeCertificate> listByEmployeeId(Long employeeId) {
        return this.list(new LambdaQueryWrapper<EmployeeCertificate>()
                .eq(EmployeeCertificate::getEmployeeId, employeeId)
                .orderByAsc(EmployeeCertificate::getSortOrder)
                .orderByDesc(EmployeeCertificate::getIssueDate));
    }

    @Override
    @Transactional
    public boolean replaceForEmployee(Long employeeId, List<EmployeeCertificate> list) {
        this.remove(new LambdaQueryWrapper<EmployeeCertificate>().eq(EmployeeCertificate::getEmployeeId, employeeId));
        if (list == null || list.isEmpty()) {
            return true;
        }
        int idx = 0;
        for (EmployeeCertificate e : list) {
            e.setId(null);
            e.setEmployeeId(employeeId);
            e.setSortOrder(idx++);
        }
        return this.saveBatch(list);
    }
}
