package com.ems.service.impl;

import com.ems.entity.Permission;
import com.ems.mapper.PermissionMapper;
import com.ems.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<Permission> listAll() {
        return permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getSortOrder)
        );
    }

    @Override
    public List<Permission> listMenus() {
        return permissionMapper.selectMenus();
    }

    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public boolean save(Permission permission) {
        return permissionMapper.insert(permission) > 0;
    }

    @Override
    public boolean update(Permission permission) {
        return permissionMapper.updateById(permission) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return permissionMapper.deleteById(id) > 0;
    }

    @Override
    public List<Permission> getTree() {
        List<Permission> all = listAll();
        List<Permission> roots = all.stream()
            .filter(p -> p.getParentId() == null || p.getParentId() == 0)
            .collect(Collectors.toList());

        for (Permission root : roots) {
            root.setChecked(false);
            List<Permission> children = all.stream()
                .filter(p -> root.getId().equals(p.getParentId()))
                .collect(Collectors.toList());
            for (Permission child : children) {
                child.setChecked(false);
            }
            root.setChildren(children);
        }
        return roots;
    }
}
