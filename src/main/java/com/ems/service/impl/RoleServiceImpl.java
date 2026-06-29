package com.ems.service.impl;

import com.ems.entity.Permission;
import com.ems.entity.Role;
import com.ems.entity.RolePermission;
import com.ems.mapper.PermissionMapper;
import com.ems.mapper.RoleMapper;
import com.ems.mapper.RolePermissionMapper;
import com.ems.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public RoleServiceImpl(RoleMapper roleMapper,
                           RolePermissionMapper rolePermissionMapper,
                           PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectWithPermissionCount();
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public boolean save(Role role) {
        return roleMapper.insert(role) > 0;
    }

    @Override
    public boolean update(Role role) {
        return roleMapper.updateById(role) > 0;
    }

    @Override
    public boolean delete(Long id) {
        rolePermissionMapper.deleteByRoleId(id);
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permId : permissionIds) {
                rolePermissionMapper.insertRelation(roleId, permId);
            }
        }
        return true;
    }

    @Override
    public List<String> getPermissionCodesByRoleCode(String roleCode) {
        Role role = roleMapper.selectOne(
            new LambdaQueryWrapper<Role>().eq(Role::getCode, roleCode)
        );
        if (role == null) return new ArrayList<>();
        return permissionMapper.selectCodesByRoleId(role.getId());
    }
}
