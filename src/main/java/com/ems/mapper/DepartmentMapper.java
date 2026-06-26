package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT d.*, " +
            "(SELECT COUNT(*) FROM employee e WHERE e.department = d.code AND e.deleted = 0) as employee_count " +
            "FROM sys_department d WHERE d.deleted = 0 ORDER BY d.sort ASC")
    List<Department> selectAllWithEmployeeCount();

    @Select("SELECT d.*, " +
            "(SELECT COUNT(*) FROM employee e WHERE e.department = d.code AND e.deleted = 0) as employee_count " +
            "FROM sys_department d WHERE d.parent_code = #{parentCode} AND d.deleted = 0 ORDER BY d.sort ASC")
    List<Department> selectByParentCode(@Param("parentCode") String parentCode);

    @Select("SELECT COUNT(*) FROM sys_department WHERE parent_code = #{parentCode} AND deleted = 0")
    int countByParentCode(@Param("parentCode") String parentCode);
}