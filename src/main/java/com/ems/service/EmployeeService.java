package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.dto.EmployeeFormDTO;
import com.ems.entity.Employee;
import com.ems.vo.EmployeeDetailVO;
import com.ems.vo.EmployeeStatisticsVO;
import com.ems.vo.ReminderVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService extends IService<Employee> {

    IPage<Employee> pageQuery(String name, String employeeNo, String department, String position, Integer status, Integer page, Integer size, String sortField, String sortOrder);

    void updateSortOrder(List<Long> ids);

    EmployeeStatisticsVO statistics();

    void exportExcel(HttpServletResponse response) throws IOException;

    void importExcel(MultipartFile file) throws IOException;

    boolean addEmployee(EmployeeFormDTO form);

    boolean updateEmployee(EmployeeFormDTO form);

    boolean deleteEmployee(Long id);

    boolean deleteEmployees(List<Long> ids);

    /**
     * 批量调岗（修改部门/职位）
     * @return 受影响行数
     */
    int batchTransfer(List<Long> ids, String department, String position);

    /**
     * 批量调薪
     * @param mode fixed(固定金额 +/-) percent(百分比 +/-) set(直接设置)
     * @param amount 调整值（正数，set 模式下为直接设置的薪资）
     * @return 受影响行数
     */
    int batchAdjustSalary(List<Long> ids, String mode, java.math.BigDecimal amount);

    void decryptSensitiveData(Employee employee);

    void decryptSensitiveData(List<Employee> employees);

    void encryptSensitiveData(Employee employee);

    /**
     * 查询员工完整档案：基本信息 + 4 张子表
     */
    EmployeeDetailVO getDetail(Long id);

    /**
     * 合同/试用期到期提醒：返回 30 天内到期的合同与试用期清单
     */
    ReminderVO reminders();
}
