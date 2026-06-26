package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.Department;

import java.util.List;

public interface DepartmentService extends IService<Department> {

    List<Department> getAllWithEmployeeCount();

    List<Department> buildTree();

    Department getByCode(String code);

    boolean saveDepartment(Department department);

    boolean updateDepartment(Department department);

    boolean deleteDepartment(Long id);
}