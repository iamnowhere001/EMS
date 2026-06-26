package com.ems.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.EncryptionUtil;
import com.ems.config.CacheConfig;
import com.ems.dto.EmployeeExcelDTO;
import com.ems.dto.EmployeeFormDTO;
import com.ems.entity.Employee;
import com.ems.mapper.EmployeeMapper;
import com.ems.service.EmployeeCertificateService;
import com.ems.service.EmployeeContractService;
import com.ems.service.EmployeeEducationService;
import com.ems.service.EmployeeFamilyService;
import com.ems.service.EmployeeProbationService;
import com.ems.service.EmployeeService;
import com.ems.service.EmployeeWorkExperienceService;
import com.ems.util.EmployeeChangeLogUtil;
import com.ems.vo.EmployeeDetailVO;
import com.ems.vo.EmployeeStatisticsVO;
import com.ems.vo.ReminderVO;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private EncryptionUtil encryptionUtil;
    private final EmployeeEducationService educationService;
    private final EmployeeWorkExperienceService workExperienceService;
    private final EmployeeFamilyService familyService;
    private final EmployeeCertificateService certificateService;
    private final EmployeeContractService contractService;
    private final EmployeeProbationService probationService;

    public EmployeeServiceImpl(EncryptionUtil encryptionUtil,
                               EmployeeEducationService educationService,
                               EmployeeWorkExperienceService workExperienceService,
                               EmployeeFamilyService familyService,
                               EmployeeCertificateService certificateService,
                               EmployeeContractService contractService,
                               EmployeeProbationService probationService) {
        this.encryptionUtil = encryptionUtil;
        this.educationService = educationService;
        this.workExperienceService = workExperienceService;
        this.familyService = familyService;
        this.certificateService = certificateService;
        this.contractService = contractService;
        this.probationService = probationService;
    }

    @PostConstruct
    public void ensureEncryptionUtil() {
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public boolean save(Employee employee) {
        if (!StringUtils.hasText(employee.getEmployeeNo())) {
            employee.setEmployeeNo(generateEmployeeNo());
        }
        if (employee.getSortOrder() == null || employee.getSortOrder() == 0) {
            employee.setSortOrder(generateSortOrder());
        }
        // 校验手机号唯一性（仅校验未删除记录）
        validatePhoneUnique(employee.getPhone(), null);
        encryptSensitiveData(employee);
        return super.save(employee);
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public boolean saveBatch(Collection<Employee> entityList) {
        entityList.forEach(employee -> {
            if (!StringUtils.hasText(employee.getEmployeeNo())) {
                employee.setEmployeeNo(generateEmployeeNo());
            }
            if (employee.getSortOrder() == null || employee.getSortOrder() == 0) {
                employee.setSortOrder(generateSortOrder());
            }
            encryptSensitiveData(employee);
        });
        return super.saveBatch(entityList);
    }

    private String generateEmployeeNo() {
        Integer maxNo = baseMapper.selectMaxEmployeeNo();
        int nextNo = (maxNo == null ? 0 : maxNo) + 1;
        return String.format("%04d", nextNo);
    }

    private Integer generateSortOrder() {
        Integer maxSort = baseMapper.selectMaxSortOrder();
        return (maxSort == null ? 0 : maxSort) + 1;
    }

    @Override
    public IPage<Employee> pageQuery(String name, String employeeNo, String department, String position, Integer status, Integer page, Integer size, String sortField, String sortOrder) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Employee::getName, name)
               .like(StringUtils.hasText(employeeNo), Employee::getEmployeeNo, employeeNo)
               .eq(StringUtils.hasText(department), Employee::getDepartment, department)
               .eq(StringUtils.hasText(position), Employee::getPosition, position)
               .eq(status != null, Employee::getStatus, status);
        applySort(wrapper, sortField, sortOrder);
        IPage<Employee> result = this.page(new Page<>(page, size), wrapper);
        decryptSensitiveData(result.getRecords());
        return result;
    }

    private void applySort(LambdaQueryWrapper<Employee> wrapper, String sortField, String sortOrder) {
        if (!StringUtils.hasText(sortField)) {
            wrapper.orderByAsc(Employee::getSortOrder).orderByDesc(Employee::getCreateTime);
            return;
        }
        boolean isAsc = "ascending".equalsIgnoreCase(sortOrder) || "asc".equalsIgnoreCase(sortOrder);
        switch (sortField) {
            case "age":
                wrapper.orderBy(true, isAsc, Employee::getAge);
                break;
            case "salary":
                wrapper.orderBy(true, isAsc, Employee::getSalary);
                break;
            case "hireDate":
                wrapper.orderBy(true, isAsc, Employee::getHireDate);
                break;
            case "workYears":
                wrapper.orderBy(true, isAsc, Employee::getHireDate);
                break;
            default:
                wrapper.orderByAsc(Employee::getSortOrder).orderByDesc(Employee::getCreateTime);
        }
    }

    @Override
    public void updateSortOrder(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            Employee employee = new Employee();
            employee.setId(ids.get(i));
            employee.setSortOrder(i + 1);
            baseMapper.updateById(employee);
        }
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_STATISTICS, key = "'overview'", unless = "#result == null")
    public EmployeeStatisticsVO statistics() {
        EmployeeStatisticsVO vo = new EmployeeStatisticsVO();
        vo.setTotalCount(this.count());
        vo.setActiveCount(lambdaQuery().eq(Employee::getStatus, 1).count());
        vo.setDepartmentCount(lambdaQuery().select(Employee::getDepartment).list()
                .stream()
                .map(Employee::getDepartment)
                .filter(StringUtils::hasText)
                .distinct()
                .count());
        vo.setPositionCount(lambdaQuery().select(Employee::getPosition).list()
                .stream()
                .map(Employee::getPosition)
                .filter(StringUtils::hasText)
                .distinct()
                .count());
        vo.setMaleCount(lambdaQuery().eq(Employee::getGender, 1).count());
        vo.setFemaleCount(lambdaQuery().eq(Employee::getGender, 0).count());

        List<BigDecimal> salaries = baseMapper.selectObjs(
                new LambdaQueryWrapper<Employee>().select(Employee::getSalary)
        ).stream()
                .filter(Objects::nonNull)
                .map(o -> (BigDecimal) o)
                .collect(Collectors.toList());

        BigDecimal totalSalary = salaries.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSalary(totalSalary);
        if (!salaries.isEmpty()) {
            vo.setAvgSalary(totalSalary.divide(new BigDecimal(salaries.size()), 2, java.math.RoundingMode.HALF_UP));
        } else {
            vo.setAvgSalary(BigDecimal.ZERO);
        }

        vo.setDepartmentDistribution(baseMapper.countByDepartment());
        vo.setPositionDistribution(baseMapper.countByPosition());
        vo.setRankDistribution(baseMapper.countByRank());
        vo.setGenderDistribution(baseMapper.countByGender());
        vo.setAgeDistribution(baseMapper.countByAgeRange());
        vo.setHireTrend(baseMapper.countByHireMonth());
        return vo;
    }

    @Override
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<Employee> list = this.list();
        decryptSensitiveData(list);
        List<EmployeeExcelDTO> excelList = list.stream().map(this::convertToExcelDTO).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("员工信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), EmployeeExcelDTO.class)
                .sheet("员工信息")
                .doWrite(excelList);
    }

    @Override
    public void importExcel(MultipartFile file) throws IOException {
        List<Employee> list = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), EmployeeExcelDTO.class, new ReadListener<EmployeeExcelDTO>() {
            @Override
            public void invoke(EmployeeExcelDTO dto, AnalysisContext context) {
                Employee employee = convertToEntity(dto);
                if (StringUtils.hasText(employee.getName())) {
                    list.add(employee);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        }).sheet().doRead();

        if (!list.isEmpty()) {
            this.saveBatch(list);
        }
    }

    private EmployeeExcelDTO convertToExcelDTO(Employee employee) {
        EmployeeExcelDTO dto = new EmployeeExcelDTO();
        BeanUtils.copyProperties(employee, dto);
        dto.setGender(employee.getGender() != null && employee.getGender() == 1 ? "男" : "女");
        dto.setStatus(employee.getStatus() != null && employee.getStatus() == 1 ? "在职" : "离职");
        return dto;
    }

    private Employee convertToEntity(EmployeeExcelDTO dto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);
        if (StringUtils.hasText(dto.getGender())) {
            employee.setGender("男".equals(dto.getGender()) ? 1 : 0);
        }
        if (StringUtils.hasText(dto.getStatus())) {
            employee.setStatus("在职".equals(dto.getStatus()) ? 1 : 0);
        }
        if (employee.getStatus() == null) {
            employee.setStatus(1);
        }
        if (employee.getGender() == null) {
            employee.setGender(1);
        }
        return employee;
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public boolean addEmployee(EmployeeFormDTO form) {
        if (form == null || form.getEmployee() == null) return false;
        Employee employee = form.getEmployee();
        boolean ok = this.save(employee);
        if (ok && employee.getId() != null) {
            Long empId = employee.getId();
            educationService.replaceForEmployee(empId, form.getEducations());
            workExperienceService.replaceForEmployee(empId, form.getWorkExperiences());
            familyService.replaceForEmployee(empId, form.getFamilies());
            certificateService.replaceForEmployee(empId, form.getCertificates());
            contractService.replaceForEmployee(empId, form.getContracts());
            probationService.replaceForEmployee(empId, form.getProbations());
        }
        return ok;
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public boolean updateEmployee(EmployeeFormDTO form) {
        if (form == null || form.getEmployee() == null) return false;
        Employee employee = form.getEmployee();
        if (employee.getId() == null) return false;
        // 校验手机号唯一性（排除自身）
        validatePhoneUnique(employee.getPhone(), employee.getId());
        encryptSensitiveData(employee);
        boolean ok = this.updateById(employee);
        if (ok) {
            Long empId = employee.getId();
            educationService.replaceForEmployee(empId, form.getEducations());
            workExperienceService.replaceForEmployee(empId, form.getWorkExperiences());
            familyService.replaceForEmployee(empId, form.getFamilies());
            certificateService.replaceForEmployee(empId, form.getCertificates());
            contractService.replaceForEmployee(empId, form.getContracts());
            probationService.replaceForEmployee(empId, form.getProbations());
        }
        return ok;
    }

    /**
     * 校验手机号唯一性（仅校验未删除记录）
     * @param phone 手机号
     * @param excludeId 排除的员工ID（更新时排除自身）
     */
    private void validatePhoneUnique(String phone, Long excludeId) {
        if (!StringUtils.hasText(phone)) {
            return; // 手机号为空时不校验
        }
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getPhone, phone)
               .eq(Employee::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(Employee::getId, excludeId);
        }
        long count = this.count(wrapper);
        if (count > 0) {
            throw new com.ems.common.BusinessException(400, "手机号已被其他员工使用");
        }
    }

    @Override
    public EmployeeDetailVO getDetail(Long id) {
        EmployeeDetailVO vo = new EmployeeDetailVO();
        Employee employee = this.getById(id);
        if (employee == null) return vo;
        decryptSensitiveData(employee);
        vo.setEmployee(employee);
        vo.setEducations(educationService.listByEmployeeId(id));
        vo.setWorkExperiences(workExperienceService.listByEmployeeId(id));
        vo.setFamilies(familyService.listByEmployeeId(id));
        vo.setCertificates(certificateService.listByEmployeeId(id));
        vo.setContracts(contractService.listByEmployeeId(id));
        vo.setProbations(probationService.listByEmployeeId(id));
        return vo;
    }

    @Override
    public ReminderVO reminders() {
        ReminderVO vo = new ReminderVO();
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(30);

        // 收集在职员工的合同
        List<Employee> active = this.lambdaQuery().eq(Employee::getStatus, 1).list();
        if (active.isEmpty()) {
            return vo;
        }
        Map<Long, Employee> empMap = active.stream().collect(Collectors.toMap(Employee::getId, e -> e));

        // 30 天内到期的合同
        for (Employee emp : active) {
            List<com.ems.entity.EmployeeContract> contracts = contractService.listByEmployeeId(emp.getId());
            for (com.ems.entity.EmployeeContract c : contracts) {
                if (c.getStatus() == null || c.getStatus() == 2) continue; // 已终止忽略
                if (c.getEndDate() == null) continue; // 无固定期跳过
                if (c.getEndDate().isAfter(threshold)) continue;
                long days = ChronoUnit.DAYS.between(today, c.getEndDate());
                ReminderVO.ContractReminderItem item = new ReminderVO.ContractReminderItem();
                item.setEmployeeId(emp.getId());
                item.setEmployeeName(emp.getName());
                item.setEmployeeNo(emp.getEmployeeNo());
                item.setContractId(c.getId());
                item.setContractType(c.getContractType());
                item.setContractNo(c.getContractNo());
                item.setStartDate(c.getStartDate() == null ? null : c.getStartDate().toString());
                item.setEndDate(c.getEndDate() == null ? null : c.getEndDate().toString());
                item.setDaysToExpire((int) days);
                vo.getExpiringContracts().add(item);
            }

            // 试用期 7 天内结束
            List<com.ems.entity.EmployeeProbation> probs = probationService.listByEmployeeId(emp.getId());
            for (com.ems.entity.EmployeeProbation p : probs) {
                if (p.getResult() != null) continue; // 已有结果，跳过
                if (p.getEndDate() == null) continue;
                if (p.getEndDate().isAfter(today.plusDays(7))) continue;
                long days = ChronoUnit.DAYS.between(today, p.getEndDate());
                ReminderVO.ProbationReminderItem item = new ReminderVO.ProbationReminderItem();
                item.setEmployeeId(emp.getId());
                item.setEmployeeName(emp.getName());
                item.setEmployeeNo(emp.getEmployeeNo());
                item.setProbationId(p.getId());
                item.setStartDate(p.getStartDate() == null ? null : p.getStartDate().toString());
                item.setEndDate(p.getEndDate() == null ? null : p.getEndDate().toString());
                item.setDaysToEnd((int) days);
                vo.getEndingProbations().add(item);
            }
        }

        vo.setTotalExpiringContracts(vo.getExpiringContracts().size());
        vo.setTotalEndingProbations(vo.getEndingProbations().size());
        return vo;
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public boolean deleteEmployee(Long id) {
        return this.removeById(id);
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public boolean deleteEmployees(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public int batchTransfer(List<Long> ids, String department, String position) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        for (Long id : ids) {
            Employee update = new Employee();
            update.setId(id);
            update.setDepartment(department);
            update.setPosition(position);
            count += baseMapper.updateById(update);
        }
        return count;
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_STATISTICS, allEntries = true)
    public int batchAdjustSalary(List<Long> ids, String mode, BigDecimal amount) {
        if (ids == null || ids.isEmpty()) return 0;
        if (amount == null) return 0;
        int count = 0;
        for (Long id : ids) {
            Employee exist = baseMapper.selectById(id);
            if (exist == null) continue;
            BigDecimal current = exist.getSalary() == null ? BigDecimal.ZERO : exist.getSalary();
            BigDecimal newSalary;
            switch (mode) {
                case "fixed":
                    newSalary = current.add(amount);
                    break;
                case "percent":
                    // amount 视为百分比，如 10 表示 +10%
                    newSalary = current.multiply(BigDecimal.ONE.add(amount.divide(new BigDecimal(100))));
                    break;
                case "set":
                    newSalary = amount;
                    break;
                default:
                    throw new com.ems.common.BusinessException(400, "不支持的调薪模式: " + mode);
            }
            if (newSalary.compareTo(BigDecimal.ZERO) < 0) {
                newSalary = BigDecimal.ZERO;
            }
            newSalary = newSalary.setScale(2, java.math.RoundingMode.HALF_UP);
            Employee update = new Employee();
            update.setId(id);
            update.setSalary(newSalary);
            count += baseMapper.updateById(update);
        }
        return count;
    }

    @Override
    public void decryptSensitiveData(Employee employee) {
        if (employee == null) return;
        if (employee.getIdCard() != null && !employee.getIdCard().isEmpty()) {
            try {
                employee.setIdCard(EncryptionUtil.decrypt(employee.getIdCard()));
            } catch (Exception e) {
            }
        }
        if (employee.getBankCard() != null && !employee.getBankCard().isEmpty()) {
            try {
                employee.setBankCard(EncryptionUtil.decrypt(employee.getBankCard()));
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void decryptSensitiveData(List<Employee> employees) {
        if (employees == null) return;
        employees.forEach(this::decryptSensitiveData);
    }

    @Override
    public void encryptSensitiveData(Employee employee) {
        if (employee == null) return;
        if (employee.getIdCard() != null && !employee.getIdCard().isEmpty()) {
            employee.setIdCard(EncryptionUtil.encrypt(employee.getIdCard()));
        }
        if (employee.getBankCard() != null && !employee.getBankCard().isEmpty()) {
            employee.setBankCard(EncryptionUtil.encrypt(employee.getBankCard()));
        }
    }

    public String buildChangedFields(Employee oldEmployee, Employee newEmployee) {
        return EmployeeChangeLogUtil.buildChangedFields(oldEmployee, newEmployee);
    }
}
