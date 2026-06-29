-- 为所有在职员工补齐默认登录账号，并约束一个员工只能绑定一个系统账号。
-- 默认账号规则：
-- 1. 员工姓名唯一时，账号名 = 员工姓名；
-- 2. 员工姓名重复时，账号名 = 员工姓名_工号；
-- 3. 默认密码 = 123456，角色 = EMPLOYEE，数据范围 = 仅本人。

SET @default_password = '$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO';

CREATE TEMPORARY TABLE tmp_employee_default_account AS
SELECT
    e.id AS employee_id,
    e.name AS employee_name,
    CASE
        WHEN name_stat.name_count = 1 THEN e.name
        ELSE CONCAT(e.name, '_', e.employee_no)
    END AS default_username
FROM employee e
JOIN (
    SELECT name, COUNT(*) AS name_count
    FROM employee
    WHERE deleted = 0
    GROUP BY name
) name_stat ON name_stat.name = e.name
WHERE e.deleted = 0;

-- 已绑定员工档案的旧账号，先规范昵称、角色和状态；用户名无冲突时同步为默认用户名。
UPDATE sys_user u
JOIN tmp_employee_default_account t ON t.employee_id = u.employee_id
LEFT JOIN sys_user conflict_user
    ON conflict_user.username = t.default_username
   AND conflict_user.id <> u.id
SET
    u.username = IF(conflict_user.id IS NULL, t.default_username, u.username),
    u.nickname = t.employee_name,
    u.role = 'EMPLOYEE',
    u.status = IFNULL(u.status, 1),
    u.dept_data_scope = IFNULL(u.dept_data_scope, 0),
    u.remark = IFNULL(u.remark, '员工默认账号');

-- 如果历史上已存在“姓名同名但未绑定员工档案”的账号，直接绑定为员工默认账号。
UPDATE sys_user u
JOIN tmp_employee_default_account t ON t.default_username = u.username
LEFT JOIN sys_user bound_user ON bound_user.employee_id = t.employee_id
SET
    u.nickname = t.employee_name,
    u.employee_id = t.employee_id,
    u.role = 'EMPLOYEE',
    u.status = IFNULL(u.status, 1),
    u.dept_data_scope = IFNULL(u.dept_data_scope, 0),
    u.remark = IFNULL(u.remark, '员工默认账号')
WHERE u.employee_id IS NULL
  AND bound_user.id IS NULL;

-- 未绑定账号的员工补默认账号。
INSERT INTO sys_user (
    username, password, nickname, employee_id, dept_data_scope, remark, role, status
)
SELECT
    t.default_username,
    @default_password,
    t.employee_name,
    t.employee_id,
    0,
    '员工默认账号，默认密码：123456',
    'EMPLOYEE',
    1
FROM tmp_employee_default_account t
LEFT JOIN sys_user bound_user ON bound_user.employee_id = t.employee_id
LEFT JOIN sys_user name_user ON name_user.username = t.default_username
WHERE bound_user.id IS NULL
  AND name_user.id IS NULL;

DROP TEMPORARY TABLE tmp_employee_default_account;

-- 历史角色兼容修正。
UPDATE sys_user SET role = 'EMPLOYEE' WHERE role = 'USER';

-- 增加员工账号绑定唯一约束，避免后续一个员工生成多个账号。
SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'sys_user'
      AND index_name = 'uk_sys_user_employee_id'
);
SET @sql = IF(
    @index_exists = 0,
    'ALTER TABLE sys_user ADD UNIQUE KEY uk_sys_user_employee_id (employee_id)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
