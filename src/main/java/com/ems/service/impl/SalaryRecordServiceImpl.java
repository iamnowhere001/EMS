package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.Employee;
import com.ems.entity.SalaryRecord;
import com.ems.entity.SalaryStructure;
import com.ems.entity.SocialSecurityConfig;
import com.ems.mapper.SalaryRecordMapper;
import com.ems.service.EmployeeService;
import com.ems.service.SalaryRecordService;
import com.ems.service.SalaryStructureService;
import com.ems.service.SocialSecurityConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SalaryRecordServiceImpl extends ServiceImpl<SalaryRecordMapper, SalaryRecord> implements SalaryRecordService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalaryStructureService salaryStructureService;

    @Autowired
    private SocialSecurityConfigService socialSecurityConfigService;

    @Override
    public IPage<SalaryRecord> pageQuery(Long employeeId, String yearMonth, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<SalaryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(employeeId != null, SalaryRecord::getEmployeeId, employeeId);
        wrapper.eq(StringUtils.hasText(yearMonth), SalaryRecord::getYearMonth, yearMonth);
        wrapper.eq(status != null, SalaryRecord::getStatus, status);
        wrapper.orderByDesc(SalaryRecord::getYearMonth);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<SalaryRecord> listByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<SalaryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalaryRecord::getEmployeeId, employeeId);
        wrapper.orderByDesc(SalaryRecord::getYearMonth);
        return this.list(wrapper);
    }

    @Override
    public SalaryRecord getByEmployeeAndMonth(Long employeeId, String yearMonth) {
        LambdaQueryWrapper<SalaryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalaryRecord::getEmployeeId, employeeId)
               .eq(SalaryRecord::getYearMonth, yearMonth);
        return this.getOne(wrapper);
    }

    @Override
    public boolean generateMonthlySalary(String yearMonth) {
        List<Employee> employees = employeeService.list();
        SocialSecurityConfig config = socialSecurityConfigService.getByYearMonth(yearMonth);
        
        for (Employee emp : employees) {
            if (emp.getStatus() != 1) continue; // 只处理在职员工
            
            SalaryRecord existing = getByEmployeeAndMonth(emp.getId(), yearMonth);
            if (existing != null && existing.getStatus() != 0) continue;
            
            SalaryStructure structure = salaryStructureService.getByEmployeeId(emp.getId());
            
            SalaryRecord record = existing != null ? existing : new SalaryRecord();
            record.setEmployeeId(emp.getId());
            record.setYearMonth(yearMonth);
            
            // 从薪资结构获取各项
            if (structure != null) {
                record.setBaseSalary(structure.getBaseSalary() != null ? structure.getBaseSalary() : BigDecimal.ZERO);
                record.setPerformanceSalary(structure.getPerformanceSalary() != null ? structure.getPerformanceSalary() : BigDecimal.ZERO);
                record.setAllowance(structure.getAllowance() != null ? structure.getAllowance() : BigDecimal.ZERO);
                record.setSubsidy(structure.getSubsidy() != null ? structure.getSubsidy() : BigDecimal.ZERO);
            } else {
                // 如果没有薪资结构，使用员工表中的 salary 作为基本工资
                record.setBaseSalary(emp.getSalary() != null ? emp.getSalary() : BigDecimal.ZERO);
                record.setPerformanceSalary(BigDecimal.ZERO);
                record.setAllowance(BigDecimal.ZERO);
                record.setSubsidy(BigDecimal.ZERO);
            }
            
            record.setOvertimePay(BigDecimal.ZERO);
            record.setDeduction(BigDecimal.ZERO);
            
            // 计算社保公积金
            if (config != null) {
                BigDecimal grossSalary = record.getBaseSalary()
                    .add(record.getPerformanceSalary())
                    .add(record.getAllowance())
                    .add(record.getSubsidy());
                
                // 社保个人部分
                BigDecimal pensionPersonal = grossSalary.multiply(config.getPensionPersonalRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal medicalPersonal = grossSalary.multiply(config.getMedicalPersonalRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal unemploymentPersonal = grossSalary.multiply(config.getUnemploymentPersonalRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal socialSecurity = pensionPersonal.add(medicalPersonal).add(unemploymentPersonal);
                record.setSocialSecurityPersonal(socialSecurity);
                
                // 公积金个人部分
                BigDecimal housingFund = grossSalary.multiply(config.getHousingFundPersonalRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                record.setHousingFundPersonal(housingFund);
                
                // 计算个税（简化版：应纳税所得额 = 工资 - 社保 - 公积金 - 5000起征点）
                BigDecimal taxableIncome = grossSalary.subtract(socialSecurity).subtract(housingFund).subtract(new BigDecimal("5000"));
                BigDecimal tax = BigDecimal.ZERO;
                if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
                    // 简化税率 3%
                    tax = taxableIncome.multiply(new BigDecimal("0.03")).setScale(2, RoundingMode.HALF_UP);
                }
                record.setTax(tax);
                
                // 实发工资
                BigDecimal actualSalary = grossSalary.subtract(socialSecurity).subtract(housingFund).subtract(tax);
                record.setActualSalary(actualSalary);
            } else {
                record.setSocialSecurityPersonal(BigDecimal.ZERO);
                record.setHousingFundPersonal(BigDecimal.ZERO);
                record.setTax(BigDecimal.ZERO);
                BigDecimal grossSalary = record.getBaseSalary()
                    .add(record.getPerformanceSalary())
                    .add(record.getAllowance())
                    .add(record.getSubsidy());
                record.setActualSalary(grossSalary);
            }
            
            record.setStatus(0); // 草稿
            this.saveOrUpdate(record);
        }
        
        return true;
    }

    @Override
    public boolean confirmSalary(Long id) {
        SalaryRecord record = this.getById(id);
        if (record == null || record.getStatus() != 0) {
            return false;
        }
        record.setStatus(1);
        return this.updateById(record);
    }

    @Override
    public boolean batchConfirm(List<Long> ids) {
        for (Long id : ids) {
            confirmSalary(id);
        }
        return true;
    }

    @Override
    public boolean batchPay(List<Long> ids) {
        for (Long id : ids) {
            SalaryRecord record = this.getById(id);
            if (record != null && record.getStatus() == 1) {
                record.setStatus(2);
                this.updateById(record);
            }
        }
        return true;
    }
}
