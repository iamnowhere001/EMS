-- ============================================================
-- RBAC 权限管理数据库迁移脚本（兼容 MySQL 9.6）
-- ============================================================

-- 1. sys_role 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色编码（唯一）',
    description VARCHAR(255) COMMENT '角色描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 2. sys_permission 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    name VARCHAR(100) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL COMMENT '权限编码（唯一）',
    type TINYINT NOT NULL COMMENT '类型：1=菜单，2=按钮，3=API',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID，0表示顶级',
    path VARCHAR(200) COMMENT '路由路径或API路径',
    icon VARCHAR(50) COMMENT '图标名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_code (code),
    KEY idx_parent (parent_id),
    KEY idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 3. sys_role_permission 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    KEY idx_role (role_id),
    KEY idx_perm (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 4. sys_user 表增加字段（兼容 MySQL 9.6，用存储过程）
DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS add_column_if_not_exists()
BEGIN
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='ems_db' AND TABLE_NAME='sys_user' AND COLUMN_NAME='employee_id') THEN
        ALTER TABLE sys_user ADD COLUMN employee_id BIGINT DEFAULT NULL COMMENT '关联员工ID' AFTER nickname;
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='ems_db' AND TABLE_NAME='sys_user' AND COLUMN_NAME='dept_data_scope') THEN
        ALTER TABLE sys_user ADD COLUMN dept_data_scope TINYINT DEFAULT 0 COMMENT '数据权限：0=仅本人，1=本部门，2=本部门及下级，3=全部' AFTER employee_id;
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='ems_db' AND TABLE_NAME='sys_user' AND COLUMN_NAME='remark') THEN
        ALTER TABLE sys_user ADD COLUMN remark VARCHAR(255) DEFAULT NULL COMMENT '备注' AFTER dept_data_scope;
    END IF;
END$$
DELIMITER ;

CALL add_column_if_not_exists();
DROP PROCEDURE add_column_if_not_exists;

-- ============================================================
-- 种子数据：5 种角色
-- ============================================================
INSERT INTO sys_role (name, code, description, sort_order, status) VALUES
('超级管理员', 'SUPER_ADMIN', '系统最高权限，所有功能均可访问', 1, 1),
('HR管理员',   'HR_ADMIN',   '负责人事、组织、考勤、请假、薪资和系统用户，不维护角色权限', 2, 1),
('HR专员',     'HR_SPECIALIST', '负责员工基础资料、考勤和请假流转，不查看薪资和系统设置', 3, 1),
('部门经理',   'DEPT_MANAGER', '查看本部门员工和考勤，审批本部门请假', 4, 1),
('普通员工',   'EMPLOYEE',    '仅可查看个人中心、本人打卡、提交/撤销请假', 5, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

-- ============================================================
-- 种子数据：权限列表（菜单 + 按钮 + API）
-- ============================================================

-- 顶级菜单权限 (type=1)
INSERT INTO sys_permission (name, code, type, parent_id, path, icon, sort_order) VALUES
('个人中心',   'menu:personal',      1, 0, '/personal',      'HomeFilled',   10),
('数据看板',   'menu:dashboard',     1, 0, '/dashboard',     'DataLine',     20),
('员工管理',   'menu:employee',      1, 0, '/employee',      'UserFilled',   30),
('组织设置',   'menu:organization',  1, 0, '/organization',  'OfficeBuilding',40),
('流程管理',   'menu:workflow',      1, 0, '/workflow',      'Document',     50),
('考勤管理',   'menu:attendance',    1, 0, '/attendance',    'Clock',        60),
('请假管理',   'menu:leave',         1, 0, '/leave',         'Document',     70),
('薪资社保',   'menu:salary',        1, 0, '/salary',        'Money',        80),
('系统设置',   'menu:system',        1, 0, '/system',        'Setting',      90)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 个人中心子权限 (type=3 API)
SET @personal_menu = (SELECT id FROM sys_permission WHERE code='menu:personal');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看个人中心', 'personal:view', 3, @personal_menu, '/api/personal/**', 10)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 员工管理子权限
SET @employee_menu = (SELECT id FROM sys_permission WHERE code='menu:employee');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看员工列表',   'employee:view',    3, @employee_menu, '/api/employee/page',   10),
('新增员工',       'employee:create',  3, @employee_menu, '/api/employee/create', 20),
('编辑员工',       'employee:edit',    3, @employee_menu, '/api/employee/update', 30),
('删除员工',       'employee:delete',  3, @employee_menu, '/api/employee/delete', 40),
('导出员工',       'employee:export',  3, @employee_menu, '/api/employee/export', 50),
('导入员工',       'employee:import',  3, @employee_menu, '/api/employee/import', 60),
('查看员工详情',   'employee:detail',  3, @employee_menu, '/api/employee/get',    70)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 组织设置子权限
SET @org_menu = (SELECT id FROM sys_permission WHERE code='menu:organization');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看组织架构', 'org:view',   3, @org_menu, '/api/department/**', 10),
('管理组织架构', 'org:manage', 3, @org_menu, '/api/department/**', 20)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 入转调离流程子权限
SET @workflow_menu = (SELECT id FROM sys_permission WHERE code='menu:workflow');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看流程台账', 'workflow:view',   3, @workflow_menu, '/api/workflow/page',   10),
('管理流程变更', 'workflow:manage', 3, @workflow_menu, '/api/workflow/**',     20)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 考勤管理子权限
SET @att_menu = (SELECT id FROM sys_permission WHERE code='menu:attendance');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看考勤',     'attendance:view',    3, @att_menu, '/api/attendance/**', 10),
('管理考勤',     'attendance:manage',  3, @att_menu, '/api/attendance/**', 20),
('打卡',         'attendance:checkin', 3, @att_menu, '/api/attendance/create', 30)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 请假管理子权限
SET @leave_menu = (SELECT id FROM sys_permission WHERE code='menu:leave');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('提交请假', 'leave:submit',  3, @leave_menu, '/api/leave/submit',   10),
('查看请假', 'leave:view',    3, @leave_menu, '/api/leave/**',      20),
('审批请假', 'leave:approve', 3, @leave_menu, '/api/leave/approve', 30),
('拒绝请假', 'leave:reject',  3, @leave_menu, '/api/leave/reject',   40),
('撤销请假', 'leave:cancel',  3, @leave_menu, '/api/leave/cancel',   50)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 薪资管理子权限
SET @salary_menu = (SELECT id FROM sys_permission WHERE code='menu:salary');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看薪资结构', 'salary:view',    3, @salary_menu, '/api/salary/structure', 10),
('管理薪资',     'salary:manage',  3, @salary_menu, '/api/salary/record',   20),
('确认工资条',   'salary:confirm', 3, @salary_menu, '/api/salary/confirm',  30),
('发放工资',     'salary:pay',     3, @salary_menu, '/api/salary/batchPay', 40)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 系统设置子权限
SET @sys_menu = (SELECT id FROM sys_permission WHERE code='menu:system');
INSERT INTO sys_permission (name, code, type, parent_id, path, sort_order) VALUES
('查看系统设置', 'system:view',    3, @sys_menu, '/api/user/**',       10),
('管理系统用户', 'system:manage',  3, @sys_menu, '/api/user/**',       20),
('查看操作日志', 'system:log',     3, @sys_menu, '/api/operation-log/**', 30),
('角色权限管理', 'system:role',    3, @sys_menu, '/api/role/**',       40),
('查看数据看板', 'dashboard:view', 3, @sys_menu, '/api/dashboard/**',   50)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ============================================================
-- 种子数据：角色-权限关联
-- ============================================================

-- 预设角色权限以脚本为准，重复执行时先清理旧授权，避免历史权限残留
DELETE rp FROM sys_role_permission rp
JOIN sys_role r ON rp.role_id = r.id
WHERE r.code IN ('SUPER_ADMIN', 'HR_ADMIN', 'HR_SPECIALIST', 'DEPT_MANAGER', 'EMPLOYEE');

-- SUPER_ADMIN 拥有所有权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.code = 'SUPER_ADMIN';

-- HR_ADMIN：人事负责人，除角色权限配置外拥有业务管理权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.code = 'HR_ADMIN' AND p.code NOT IN ('system:role');

-- HR_SPECIALIST：员工+考勤+请假，不含薪资和导入导出
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.code = 'HR_SPECIALIST' AND p.code IN (
    'menu:personal', 'personal:view',
    'menu:employee', 'employee:view', 'employee:create', 'employee:edit', 'employee:detail',
    'menu:workflow', 'workflow:view', 'workflow:manage',
    'menu:attendance', 'attendance:view', 'attendance:manage', 'attendance:checkin',
    'menu:leave', 'leave:submit', 'leave:view', 'leave:approve', 'leave:reject', 'leave:cancel',
    'menu:dashboard', 'dashboard:view'
);

-- DEPT_MANAGER：个人中心+本部门考勤+审批本部门请假
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.code = 'DEPT_MANAGER' AND p.code IN (
    'menu:personal', 'personal:view',
    'menu:employee', 'employee:view', 'employee:detail',
    'menu:workflow', 'workflow:view',
    'menu:attendance', 'attendance:view', 'attendance:checkin',
    'menu:leave', 'leave:submit', 'leave:view', 'leave:approve', 'leave:reject', 'leave:cancel'
);

-- EMPLOYEE：仅个人中心+打卡+申请请假
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.code = 'EMPLOYEE' AND p.code IN (
    'menu:personal', 'personal:view',
    'menu:attendance', 'attendance:view', 'attendance:checkin',
    'menu:leave', 'leave:submit', 'leave:view', 'leave:cancel'
);

-- 将现有 admin 用户的 role 字段更新为 SUPER_ADMIN
UPDATE sys_user SET role = 'SUPER_ADMIN' WHERE username = 'admin' AND (role IS NULL OR role = 'admin' OR role = '');
UPDATE sys_user SET role = 'EMPLOYEE' WHERE role = 'USER';
UPDATE sys_user u
JOIN employee e ON e.name = u.nickname AND e.deleted = 0
SET u.employee_id = e.id
WHERE u.employee_id IS NULL
  AND u.role IN ('EMPLOYEE', 'USER', 'DEPT_MANAGER')
  AND e.id = (
      SELECT MAX(e2.id) FROM employee e2 WHERE e2.name = u.nickname AND e2.deleted = 0
  );

-- ============================================================
-- 测试账号（默认密码：123456）
-- ============================================================
INSERT INTO sys_user (username, password, nickname, role, dept_data_scope, status, remark) VALUES
('superadmin',   '$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K', '超级管理员', 'SUPER_ADMIN',   3, 1, '测试账号：超级管理员，拥有所有权限'),
('hradmin',      '$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K', 'HR管理员',   'HR_ADMIN',      2, 1, '测试账号：HR管理员，除系统管理外全部权限'),
('hrspecialist', '$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K', 'HR专员',     'HR_SPECIALIST', 2, 1, '测试账号：HR专员，可管理员工和考勤'),
('deptmanager',  '$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K', '部门经理',   'DEPT_MANAGER',  1, 1, '测试账号：部门经理，可审批本部门请假'),
('employee',     '$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K', '普通员工',   'EMPLOYEE',      0, 1, '测试账号：普通员工，仅个人中心/打卡/请假')
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    role = VALUES(role),
    dept_data_scope = VALUES(dept_data_scope),
    remark = VALUES(remark),
    status = 1;
