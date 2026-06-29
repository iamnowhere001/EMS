package com.ems.service;

import com.ems.entity.Permission;
import com.ems.entity.Role;
import java.util.List;

public interface RoleService {

    List<Role> listAll();

    Role getById(Long id);

    boolean save(Role role);

    boolean update(Role role);

    boolean delete(Long id);

    List<Permission> getRolePermissions(Long roleId);

    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    List<String> getPermissionCodesByRoleCode(String roleCode);
}
