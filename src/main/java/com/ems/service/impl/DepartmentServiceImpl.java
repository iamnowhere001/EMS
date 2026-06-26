package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.BusinessException;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.mapper.DepartmentMapper;
import com.ems.mapper.EmployeeMapper;
import com.ems.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final EmployeeMapper employeeMapper;

    public DepartmentServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public List<Department> getAllWithEmployeeCount() {
        return baseMapper.selectAllWithEmployeeCount();
    }

    @Override
    public Department getByCode(String code) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Department>()
                .eq(Department::getCode, code)
                .eq(Department::getDeleted, 0));
    }

    @Override
    public boolean saveDepartment(Department department) {
        if (!StringUtils.hasText(department.getCode())) {
            throw new BusinessException(400, "部门编码不能为空");
        }
        if (!StringUtils.hasText(department.getName())) {
            throw new BusinessException(400, "部门名称不能为空");
        }

        Department existing = getByCode(department.getCode());
        if (existing != null) {
            throw new BusinessException(400, "部门编码已存在");
        }

        if (department.getSort() == null) {
            department.setSort(0);
        }
        if (department.getStatus() == null) {
            department.setStatus(1);
        }

        return this.save(department);
    }

    @Override
    public boolean updateDepartment(Department department) {
        if (department.getId() == null) {
            throw new BusinessException(400, "部门ID不能为空");
        }

        Department existing = this.getById(department.getId());
        if (existing == null) {
            throw new BusinessException(400, "部门不存在");
        }

        Department codeExist = getByCode(department.getCode());
        if (codeExist != null && !codeExist.getId().equals(department.getId())) {
            throw new BusinessException(400, "部门编码已被使用");
        }

        department.setCode(existing.getCode());
        return this.updateById(department);
    }

    @Override
    public boolean deleteDepartment(Long id) {
        Department department = this.getById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在");
        }

        int childCount = baseMapper.countByParentCode(department.getCode());
        if (childCount > 0) {
            throw new BusinessException(400, "该部门下存在子部门，无法删除");
        }

        long employeeCount = employeeMapper.selectCount(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getDepartment, department.getCode())
                .eq(Employee::getDeleted, 0));
        if (employeeCount > 0) {
            throw new BusinessException(400, "该部门下存在员工，无法删除");
        }

        return this.removeById(id);
    }

    @Override
    public List<Department> buildTree() {
        List<Department> all = getAllWithEmployeeCount();

        Map<String, Department> codeMap = new HashMap<>();
        Map<String, List<Department>> parentMap = new HashMap<>();

        for (Department dept : all) {
            codeMap.put(dept.getCode(), dept);
            String parentCode = dept.getParentCode();
            parentMap.computeIfAbsent(parentCode, k -> new ArrayList<>()).add(dept);
        }

        List<Department> tree = new ArrayList<>();
        List<Department> roots = parentMap.getOrDefault(null, parentMap.getOrDefault("", new ArrayList<>()));
        roots.sort((a, b) -> Integer.compare(a.getSort() != null ? a.getSort() : 0, b.getSort() != null ? b.getSort() : 0));

        for (Department root : roots) {
            tree.add(buildNode(root, parentMap));
        }

        return tree;
    }

    private Department buildNode(Department node, Map<String, List<Department>> parentMap) {
        List<Department> children = parentMap.getOrDefault(node.getCode(), new ArrayList<>());
        children.sort((a, b) -> Integer.compare(a.getSort() != null ? a.getSort() : 0, b.getSort() != null ? b.getSort() : 0));

        List<Department> childNodes = new ArrayList<>();
        for (Department child : children) {
            childNodes.add(buildNode(child, parentMap));
        }

        Department result = new Department();
        result.setId(node.getId());
        result.setCode(node.getCode());
        result.setName(node.getName());
        result.setParentCode(node.getParentCode());
        result.setLeaderId(node.getLeaderId());
        result.setLeaderName(node.getLeaderName());
        result.setSort(node.getSort());
        result.setStatus(node.getStatus());
        result.setDescription(node.getDescription());
        result.setEmployeeCount(node.getEmployeeCount());
        result.setCreateTime(node.getCreateTime());
        result.setUpdateTime(node.getUpdateTime());
        result.setChildren(childNodes);

        return result;
    }
}