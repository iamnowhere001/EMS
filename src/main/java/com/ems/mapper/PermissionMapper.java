package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("""
        SELECT p.* FROM sys_permission p
        WHERE p.id IN (
            SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}
        )
        ORDER BY p.sort_order ASC
    """)
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    @Select("""
        SELECT p.code FROM sys_permission p
        WHERE p.id IN (
            SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}
        )
    """)
    List<String> selectCodesByRoleId(@Param("roleId") Long roleId);

    @Select("""
        SELECT p.* FROM sys_permission p
        WHERE p.type = 1
        ORDER BY p.sort_order ASC
    """)
    List<Permission> selectMenus();

    @Select("""
        SELECT DISTINCT code FROM sys_permission p
        WHERE p.id IN (
            SELECT permission_id FROM sys_role_permission rp
            JOIN sys_role r ON rp.role_id = r.id
            WHERE r.code = #{roleCode}
        )
    """)
    List<String> selectCodesByRoleCode(@Param("roleCode") String roleCode);
}
