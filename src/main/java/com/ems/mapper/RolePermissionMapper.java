package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    @Insert("""
        INSERT INTO sys_role_permission (role_id, permission_id)
        VALUES (#{roleId}, #{permissionId})
    """)
    int insertRelation(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Select("""
        SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}
    """)
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
