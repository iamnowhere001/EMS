package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("""
        SELECT r.*, COUNT(rp.permission_id) AS permission_count
        FROM sys_role r
        LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
        GROUP BY r.id, r.name, r.code, r.description, r.sort_order, r.status, r.create_time, r.update_time
        ORDER BY r.sort_order ASC
    """)
    List<Role> selectWithPermissionCount();

    @Select("SELECT code FROM sys_role WHERE id = #{id}")
    String selectCodeById(@Param("id") Long id);
}
