package com.ems.service;

import com.ems.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<Permission> listAll();
    List<Permission> listMenus();
    Permission getById(Long id);
    boolean save(Permission permission);
    boolean update(Permission permission);
    boolean delete(Long id);
    List<Permission> getTree();
}
