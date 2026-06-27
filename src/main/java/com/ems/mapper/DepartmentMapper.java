package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT d.id, d.code, d.name, d.parent_code, d.leader_id, d.leader_name, d.sort, d.status, " +
            "d.description, d.create_time, d.update_time, d.deleted, " +
            "(SELECT COUNT(*) FROM employee e WHERE (e.department = d.code OR e.department = d.name) " +
            "AND e.deleted = 0 AND e.status = 1) as employee_count " +
            "FROM sys_department d WHERE d.deleted = 0 ORDER BY d.sort ASC")
    List<Department> selectAllWithEmployeeCount();

    @Select("SELECT d.id, d.code, d.name, d.parent_code, d.leader_id, d.leader_name, d.sort, d.status, " +
            "d.description, d.create_time, d.update_time, d.deleted, " +
            "(SELECT COUNT(*) FROM employee e WHERE (e.department = d.code OR e.department = d.name) " +
            "AND e.deleted = 0 AND e.status = 1) as employee_count " +
            "FROM sys_department d WHERE d.parent_code = #{parentCode} AND d.deleted = 0 ORDER BY d.sort ASC")
    List<Department> selectByParentCode(@Param("parentCode") String parentCode);

    @Select("SELECT COUNT(*) FROM sys_department WHERE parent_code = #{parentCode} AND deleted = 0")
    int countByParentCode(@Param("parentCode") String parentCode);
}
