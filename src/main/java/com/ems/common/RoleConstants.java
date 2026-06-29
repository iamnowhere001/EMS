package com.ems.common;

/**
 * 角色常量定义（与 sys_role 表的 code 字段对应）
 */
public class RoleConstants {
    /** 超级管理员 */
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    /** HR管理员 */
    public static final String HR_ADMIN = "HR_ADMIN";
    /** HR专员 */
    public static final String HR_SPECIALIST = "HR_SPECIALIST";
    /** 部门经理 */
    public static final String DEPT_MANAGER = "DEPT_MANAGER";
    /** 普通员工 */
    public static final String EMPLOYEE = "EMPLOYEE";

    /** 向后兼容：旧 ADMIN 角色 */
    public static final String ADMIN = "SUPER_ADMIN";
    /** 向后兼容：旧 USER 角色 → 映射为普通员工 */
    public static final String USER = "EMPLOYEE";

    private RoleConstants() {}

    public static boolean isAdmin(String role) {
        if (role == null) return false;
        return SUPER_ADMIN.equalsIgnoreCase(role)
                || HR_ADMIN.equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role);
    }

    /** 判断是否为超级管理员（拥有所有权限） */
    public static boolean isSuperAdmin(String role) {
        return SUPER_ADMIN.equalsIgnoreCase(role);
    }
}
