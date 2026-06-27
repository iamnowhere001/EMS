CREATE DATABASE IF NOT EXISTS ems_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ems_db;

CREATE TABLE IF NOT EXISTS `employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_no` VARCHAR(10) NOT NULL COMMENT '工号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-女，1-男',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `id_card` VARCHAR(255) DEFAULT NULL COMMENT '身份证号（加密）',
    `id_card_hash` VARCHAR(64) DEFAULT NULL COMMENT '身份证号SHA256哈希（用于唯一性校验）',
    `bank_card` VARCHAR(255) DEFAULT NULL COMMENT '银行卡号（加密）',
    `bank_card_hash` VARCHAR(64) DEFAULT NULL COMMENT '银行卡号SHA256哈希（用于唯一性校验）',
    `department` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位编码',
    `rank` VARCHAR(20) DEFAULT NULL COMMENT '职级（如 P5/P6/P7）',
    `salary` DECIMAL(10,2) DEFAULT NULL COMMENT '薪资',
    `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    `sort_order` INT DEFAULT 0 COMMENT '自定义排序号',
    `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    `current_address` VARCHAR(255) DEFAULT NULL COMMENT '现住址',
    `hukou_address` VARCHAR(255) DEFAULT NULL COMMENT '户籍地址',
    `political_status` VARCHAR(20) DEFAULT NULL COMMENT '政治面貌：群众/团员/党员/民主党派',
    `marital_status` TINYINT DEFAULT 0 COMMENT '婚姻状况：0-未婚，1-已婚，2-离异，3-丧偶',
    `nation` VARCHAR(20) DEFAULT NULL COMMENT '民族',
    `native_place` VARCHAR(50) DEFAULT NULL COMMENT '籍贯',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_no` (`employee_no`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_name` (`name`),
    KEY `idx_department` (`department`),
    KEY `idx_position` (`position`),
    KEY `idx_rank` (`rank`),
    KEY `idx_id_card_hash` (`id_card_hash`),
    KEY `idx_bank_card_hash` (`bank_card_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

-- Phase 1 档案补全：教育经历子表
CREATE TABLE IF NOT EXISTS `employee_education` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `school` VARCHAR(100) NOT NULL COMMENT '学校名称',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `education` VARCHAR(20) DEFAULT NULL COMMENT '学历：高中/大专/本科/硕士/博士',
    `degree` VARCHAR(20) DEFAULT NULL COMMENT '学位：无/学士/硕士/博士',
    `start_date` DATE DEFAULT NULL COMMENT '入学日期',
    `end_date` DATE DEFAULT NULL COMMENT '毕业日期',
    `is_full_time` TINYINT DEFAULT 1 COMMENT '是否全日制：0-否，1-是',
    `sort_order` INT DEFAULT 0 COMMENT '排序（学历由高到低）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工教育经历表';

-- Phase 1 档案补全：工作经历子表
CREATE TABLE IF NOT EXISTS `employee_work_experience` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `company` VARCHAR(100) NOT NULL COMMENT '公司名称',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
    `department` VARCHAR(50) DEFAULT NULL COMMENT '部门',
    `start_date` DATE DEFAULT NULL COMMENT '入职日期',
    `end_date` DATE DEFAULT NULL COMMENT '离职日期',
    `leave_reason` VARCHAR(255) DEFAULT NULL COMMENT '离职原因',
    `sort_order` INT DEFAULT 0 COMMENT '排序（按时间倒序）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工工作经历表';

-- Phase 1 档案补全：家庭成员子表
CREATE TABLE IF NOT EXISTS `employee_family` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `name` VARCHAR(50) NOT NULL COMMENT '成员姓名',
    `relation` VARCHAR(20) NOT NULL COMMENT '与本人关系：配偶/父亲/母亲/儿子/女儿/兄弟/姐妹/其他',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-女，1-男',
    `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
    `company` VARCHAR(100) DEFAULT NULL COMMENT '工作单位',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工家庭成员表';

-- Phase 1 档案补全：技能证书子表
CREATE TABLE IF NOT EXISTS `employee_certificate` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `name` VARCHAR(100) NOT NULL COMMENT '证书名称',
    `level` VARCHAR(20) DEFAULT NULL COMMENT '证书级别：初级/中级/高级',
    `issuer` VARCHAR(100) DEFAULT NULL COMMENT '发证机构',
    `issue_date` DATE DEFAULT NULL COMMENT '发证日期',
    `expire_date` DATE DEFAULT NULL COMMENT '到期日期',
    `cert_no` VARCHAR(100) DEFAULT NULL COMMENT '证书编号',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工技能证书表';

-- Phase 2 合同与试用期：合同子表
CREATE TABLE IF NOT EXISTS `employee_contract` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `contract_no` VARCHAR(50) DEFAULT NULL COMMENT '合同编号',
    `contract_type` VARCHAR(20) NOT NULL COMMENT '合同类型：固定期限/无固定期限/实习/兼职/劳务派遣',
    `start_date` DATE NOT NULL COMMENT '合同开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '合同结束日期（无固定期限为空）',
    `probation_months` INT DEFAULT 0 COMMENT '试用期月数',
    `probation_start_date` DATE DEFAULT NULL COMMENT '试用期开始日期',
    `probation_end_date` DATE DEFAULT NULL COMMENT '试用期结束日期',
    `signed_date` DATE DEFAULT NULL COMMENT '签订日期',
    `salary` DECIMAL(10,2) DEFAULT NULL COMMENT '合同约定薪资',
    `work_location` VARCHAR(100) DEFAULT NULL COMMENT '工作地点',
    `status` TINYINT DEFAULT 1 COMMENT '合同状态：0-已到期，1-执行中，2-已终止，3-已续签',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `attachment_url` VARCHAR(255) DEFAULT NULL COMMENT '合同附件URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_end_date` (`end_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工合同信息表';

-- Phase 2 合同与试用期：试用期记录子表
CREATE TABLE IF NOT EXISTS `employee_probation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `contract_id` BIGINT DEFAULT NULL COMMENT '关联合同ID',
    `start_date` DATE NOT NULL COMMENT '试用期开始日期',
    `end_date` DATE NOT NULL COMMENT '试用期结束日期',
    `result` TINYINT DEFAULT NULL COMMENT '考核结果：0-未通过，1-通过提前转正，2-正常转正，3-延长试用期',
    `result_date` DATE DEFAULT NULL COMMENT '考核结果日期',
    `extension_end_date` DATE DEFAULT NULL COMMENT '延长后结束日期（result=3时使用）',
    `evaluator` VARCHAR(50) DEFAULT NULL COMMENT '考核人',
    `evaluation` TEXT DEFAULT NULL COMMENT '考核评语',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_contract_id` (`contract_id`),
    KEY `idx_end_date` (`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工试用期记录表';

-- Phase 3 入转调离：变更台账（调岗/调薪/转正 等）
CREATE TABLE IF NOT EXISTS `employee_workflow_change` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变更类型：TRANSFER-调岗，ADJUST_SALARY-调薪，CONFIRM-转正，LEAVE-离职',
    `effective_date` DATE NOT NULL COMMENT '生效日期',
    `before_value` TEXT DEFAULT NULL COMMENT '变更前内容快照（JSON）',
    `after_value` TEXT DEFAULT NULL COMMENT '变更后内容快照（JSON）',
    `change_summary` VARCHAR(500) DEFAULT NULL COMMENT '变更摘要（旧 → 新）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已撤销，1-已生效',
    `applicant` VARCHAR(50) DEFAULT NULL COMMENT '申请人',
    `apply_date` DATE DEFAULT NULL COMMENT '申请日期',
    `approver` VARCHAR(50) DEFAULT NULL COMMENT '审批人',
    `approve_date` DATE DEFAULT NULL COMMENT '审批日期',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工变更台账';

-- Phase 3 入转调离：离职记录
CREATE TABLE IF NOT EXISTS `employee_leave` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `leave_type` VARCHAR(20) DEFAULT '主动离职' COMMENT '离职类型：主动离职/被动辞退/合同到期/协商解除',
    `apply_date` DATE NOT NULL COMMENT '申请日期',
    `last_work_date` DATE NOT NULL COMMENT '最后工作日',
    `leave_date` DATE NOT NULL COMMENT '离职日期',
    `handover_to` BIGINT DEFAULT NULL COMMENT '工作交接人 employee_id',
    `handover_note` VARCHAR(1000) DEFAULT NULL COMMENT '交接说明',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '离职原因',
    `exit_interview_note` VARCHAR(1000) DEFAULT NULL COMMENT '离职面谈纪要',
    `applicant` VARCHAR(50) DEFAULT NULL COMMENT '申请人',
    `approver` VARCHAR(50) DEFAULT NULL COMMENT '审批人',
    `approve_date` DATE DEFAULT NULL COMMENT '审批日期',
    `social_insurance_cutoff` DATE DEFAULT NULL COMMENT '社保停缴月份',
    `housing_fund_cutoff` DATE DEFAULT NULL COMMENT '公积金停缴月份',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_leave_date` (`leave_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工离职记录';

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_operator` (`operator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS `sys_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
  `name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `parent_code` VARCHAR(50) DEFAULT NULL COMMENT '父部门编码',
  `leader_id` BIGINT DEFAULT NULL COMMENT '负责人员工ID',
  `leader_name` VARCHAR(50) DEFAULT NULL COMMENT '负责人姓名',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '部门说明',
  `employee_count` INT DEFAULT 0 COMMENT '部门人数快照字段，实际统计以员工表为准',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_code` (`parent_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS `sys_dictionary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(50) NOT NULL COMMENT '字典类型：department/position',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '字典编码',
  `name` VARCHAR(50) NOT NULL COMMENT '字典名称',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `parent_code` VARCHAR(50) DEFAULT NULL COMMENT '父级编码（如岗位所属部门编码）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_name` (`type`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典表';

-- Phase 4 考勤管理：考勤记录表
CREATE TABLE IF NOT EXISTS `employee_attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `attendance_date` DATE NOT NULL COMMENT '考勤日期',
    `check_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
    `check_out_time` DATETIME DEFAULT NULL COMMENT '签退时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-正常，1-迟到，2-早退，3-缺勤，4-请假，5-出差',
    `work_hours` DECIMAL(5,2) DEFAULT NULL COMMENT '工作时长（小时）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_date` (`employee_id`, `attendance_date`),
    KEY `idx_attendance_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工考勤记录表';

-- Phase 4 薪资结构：薪资模板表
CREATE TABLE IF NOT EXISTS `salary_structure` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    `performance_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '绩效工资',
    `allowance` DECIMAL(10,2) DEFAULT 0 COMMENT '岗位津贴',
    `subsidy` DECIMAL(10,2) DEFAULT 0 COMMENT '补贴（交通/餐饮/通讯）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪资结构表';

-- Phase 4 薪资结构：月度工资表
CREATE TABLE IF NOT EXISTS `salary_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `year_month` VARCHAR(7) NOT NULL COMMENT '年月（YYYY-MM）',
    `base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    `performance_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '绩效工资',
    `allowance` DECIMAL(10,2) DEFAULT 0 COMMENT '岗位津贴',
    `subsidy` DECIMAL(10,2) DEFAULT 0 COMMENT '补贴',
    `overtime_pay` DECIMAL(10,2) DEFAULT 0 COMMENT '加班费',
    `deduction` DECIMAL(10,2) DEFAULT 0 COMMENT '扣款',
    `social_security_personal` DECIMAL(10,2) DEFAULT 0 COMMENT '社保个人部分',
    `housing_fund_personal` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金个人部分',
    `tax` DECIMAL(10,2) DEFAULT 0 COMMENT '个税',
    `actual_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '实发工资',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已确认，2-已发放',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_month` (`employee_id`, `year_month`),
    KEY `idx_year_month` (`year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度工资记录表';

-- Phase 4 社保公积金：社保公积金配置表
CREATE TABLE IF NOT EXISTS `social_security_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `year_month` VARCHAR(7) NOT NULL COMMENT '年月（YYYY-MM）',
    `pension_base` DECIMAL(10,2) DEFAULT 0 COMMENT '养老保险基数',
    `pension_company_rate` DECIMAL(5,2) DEFAULT 16 COMMENT '养老保险公司比例(%)',
    `pension_personal_rate` DECIMAL(5,2) DEFAULT 8 COMMENT '养老保险个人比例(%)',
    `medical_base` DECIMAL(10,2) DEFAULT 0 COMMENT '医疗保险基数',
    `medical_company_rate` DECIMAL(5,2) DEFAULT 9.5 COMMENT '医疗公司比例(%)',
    `medical_personal_rate` DECIMAL(5,2) DEFAULT 2 COMMENT '医疗个人比例(%)',
    `unemployment_base` DECIMAL(10,2) DEFAULT 0 COMMENT '失业保险基数',
    `unemployment_company_rate` DECIMAL(5,2) DEFAULT 0.5 COMMENT '失业公司比例(%)',
    `unemployment_personal_rate` DECIMAL(5,2) DEFAULT 0.5 COMMENT '失业个人比例(%)',
    `injury_base` DECIMAL(10,2) DEFAULT 0 COMMENT '工伤保险基数',
    `injury_company_rate` DECIMAL(5,2) DEFAULT 0.4 COMMENT '工伤公司比例(%)',
    `maternity_base` DECIMAL(10,2) DEFAULT 0 COMMENT '生育保险基数',
    `maternity_company_rate` DECIMAL(5,2) DEFAULT 0.8 COMMENT '生育公司比例(%)',
    `housing_fund_base` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金基数',
    `housing_fund_company_rate` DECIMAL(5,2) DEFAULT 12 COMMENT '公积金公司比例(%)',
    `housing_fund_personal_rate` DECIMAL(5,2) DEFAULT 12 COMMENT '公积金个人比例(%)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_year_month` (`year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社保公积金配置表';

INSERT INTO `sys_department` (`code`, `name`, `parent_code`, `sort`, `status`) VALUES
('tech', '技术部', NULL, 1, 1),
('product', '产品部', NULL, 2, 1),
('design', '设计部', NULL, 3, 1),
('operation', '运营部', NULL, 4, 1),
('hr', '人事部', NULL, 5, 1),
('finance', '财务部', NULL, 6, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `sort` = VALUES(`sort`), `status` = VALUES(`status`);

INSERT INTO `sys_dictionary` (`type`, `code`, `name`, `sort`, `parent_code`) VALUES
('department', 'tech', '技术部', 1, NULL),
('department', 'product', '产品部', 2, NULL),
('department', 'design', '设计部', 3, NULL),
('department', 'operation', '运营部', 4, NULL),
('department', 'hr', '人事部', 5, NULL),
('department', 'finance', '财务部', 6, NULL),
('position', 'java_dev', 'Java开发', 1, 'tech'),
('position', 'frontend_dev', '前端开发', 2, 'tech'),
('position', 'product_manager', '产品经理', 3, 'product'),
('position', 'ui_designer', 'UI设计师', 4, 'design'),
('position', 'qa_engineer', '测试工程师', 5, 'tech'),
('position', 'operation_specialist', '运营专员', 6, 'operation'),
('position', 'hr_supervisor', '人事主管', 7, 'hr'),
('position', 'finance_specialist', '财务专员', 8, 'finance'),
('rank', 'P5', 'P5', 1, NULL),
('rank', 'P6', 'P6', 2, NULL),
('rank', 'P7', 'P7', 3, NULL),
('rank', 'P8', 'P8', 4, NULL)
ON DUPLICATE KEY UPDATE `name` = `name`, `parent_code` = VALUES(`parent_code`);

