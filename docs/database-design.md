# 员工信息管理系统数据库设计文档

版本：v1.0  
数据库：MySQL 8.0+  
库名：`ems_db`  
字符集：`utf8mb4`  
排序规则：`utf8mb4_unicode_ci`  
生成日期：2026-06-27

## 1. 设计目标

本数据库用于支撑员工信息管理系统的核心业务，包括员工档案、组织架构、考勤、薪资社保、合同试用期、入转调离、系统用户、字典配置和操作审计。

设计重点：

- 支持员工全生命周期管理：入职、档案维护、合同、试用期、调岗调薪、离职。
- 支持组织基础数据维护：部门、岗位、职级。
- 支持考勤与薪资核算，工资记录按员工和月份唯一。
- 支持敏感数据保护，身份证号和银行卡号加密存储，并使用 SHA-256 哈希辅助唯一性校验。
- 支持逻辑删除，员工和部门查询默认过滤 `deleted = 0`。
- 支持审计追踪，关键操作写入操作日志并记录字段变化。

## 2. 数据库总体结构

当前数据库共 17 张表：

| 模块 | 表名 | 中文名 | 说明 |
|---|---|---|---|
| 员工主档 | `employee` | 员工信息表 | 员工基础资料、联系方式、组织归属、薪资基础值 |
| 员工档案 | `employee_education` | 员工教育经历表 | 员工一对多教育经历 |
| 员工档案 | `employee_work_experience` | 员工工作经历表 | 员工一对多历史工作经历 |
| 员工档案 | `employee_family` | 员工家庭成员表 | 员工一对多家庭成员 |
| 员工档案 | `employee_certificate` | 员工技能证书表 | 员工一对多证书信息 |
| 合同试用 | `employee_contract` | 员工合同信息表 | 合同、试用期起止、合同附件 |
| 合同试用 | `employee_probation` | 员工试用期记录表 | 试用期考核结果 |
| 入转调离 | `employee_workflow_change` | 员工变更台账 | 调岗、调薪、转正、离职等变更记录 |
| 入转调离 | `employee_leave` | 员工离职记录 | 离职申请、交接、停缴日期 |
| 考勤 | `employee_attendance` | 员工考勤记录表 | 每人每天一条考勤记录 |
| 薪资社保 | `salary_structure` | 员工薪资结构表 | 员工薪资模板 |
| 薪资社保 | `salary_record` | 月度工资记录表 | 每人每月工资明细 |
| 薪资社保 | `social_security_config` | 社保公积金配置表 | 每月社保公积金基数与比例 |
| 组织设置 | `sys_department` | 部门表 | 部门树、负责人、部门状态 |
| 组织设置 | `sys_dictionary` | 系统字典表 | 部门、岗位、职级等枚举配置 |
| 系统管理 | `sys_user` | 系统用户表 | 登录账号、角色、状态 |
| 系统管理 | `operation_log` | 操作日志表 | 操作审计记录 |

## 3. 业务关系

### 3.1 核心实体关系

```text
employee 1 ── N employee_education
employee 1 ── N employee_work_experience
employee 1 ── N employee_family
employee 1 ── N employee_certificate
employee 1 ── N employee_contract
employee 1 ── N employee_probation
employee 1 ── N employee_workflow_change
employee 1 ── N employee_leave
employee 1 ── N employee_attendance
employee 1 ── N salary_structure
employee 1 ── N salary_record

sys_department 1 ── N sys_department
sys_dictionary(department) 1 ── N sys_dictionary(position)
sys_user 1 ── N operation_log
```

### 3.2 关系实现说明

- 当前表结构主要通过业务字段和普通索引维护关系，未显式定义数据库外键。
- 员工子表通过 `employee_id` 关联 `employee.id`。
- `employee.department` 存储部门编码，关联 `sys_department.code` 或 `sys_dictionary(type='department').code`。
- `employee.position` 存储岗位编码，关联 `sys_dictionary(type='position').code`。
- `employee.rank` 存储职级编码，关联 `sys_dictionary(type='rank').code`。
- `sys_department.parent_code` 关联 `sys_department.code`，用于组织树。
- `sys_dictionary.parent_code` 用于岗位归属部门，例如岗位 `java_dev` 的 `parent_code = tech`。

## 4. 统一设计规范

### 4.1 主键

所有业务表主键均使用：

```sql
id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
```

### 4.2 时间字段

多数业务表包含：

| 字段 | 类型 | 说明 |
|---|---|---|
| `create_time` | `DATETIME` | 创建时间，默认 `CURRENT_TIMESTAMP` |
| `update_time` | `DATETIME` | 更新时间，默认 `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` |

`operation_log` 只保留 `create_time`，用于记录操作发生时间。

### 4.3 逻辑删除

已配置 MyBatis-Plus 逻辑删除：

| 配置项 | 值 |
|---|---|
| 逻辑删除字段 | `deleted` |
| 未删除值 | `0` |
| 已删除值 | `1` |

当前使用逻辑删除的表：

- `employee`
- `sys_department`

业务查询默认应带上 `deleted = 0`。

### 4.4 敏感字段

| 字段 | 存储方式 | 说明 |
|---|---|---|
| `employee.id_card` | AES 加密 | 身份证号密文 |
| `employee.id_card_hash` | SHA-256 | 身份证号唯一性校验 |
| `employee.bank_card` | AES 加密 | 银行卡号密文 |
| `employee.bank_card_hash` | SHA-256 | 银行卡号唯一性校验 |
| `employee.phone` | 明文 + 唯一索引 | 手机号唯一，前端展示需脱敏 |
| `employee.email` | 明文 | 前端展示需脱敏 |

应用层通过 `EncryptionUtil` 和 `HashUtil` 写入敏感字段。查询返回前会执行解密，前端负责展示脱敏。

### 4.5 编码与枚举

- 员工工号 `employee_no` 由系统自动生成，格式为 4 位数字字符串，例如 `0001`。
- 部门、岗位、职级尽量存储编码，不存储中文名称，便于后续更名。
- 数据库中的保留关键字字段需要转义，例如：
  - `employee.rank`
  - `salary_record.year_month`
  - `social_security_config.year_month`

## 5. 表结构设计

### 5.1 `employee` 员工信息表

用途：保存员工主档信息，是人员管理、统计、考勤、薪资和流程记录的核心主表。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_no` | `VARCHAR(10)` | 是 | - | 工号，系统自动生成，不允许修改 |
| `name` | `VARCHAR(50)` | 是 | - | 姓名 |
| `age` | `INT` | 否 | `NULL` | 年龄 |
| `gender` | `TINYINT` | 否 | `0` | 性别：`0` 女，`1` 男 |
| `phone` | `VARCHAR(20)` | 否 | `NULL` | 手机号，唯一 |
| `email` | `VARCHAR(100)` | 否 | `NULL` | 邮箱 |
| `id_card` | `VARCHAR(255)` | 否 | `NULL` | 身份证号密文 |
| `id_card_hash` | `VARCHAR(64)` | 否 | `NULL` | 身份证号 SHA-256 哈希 |
| `bank_card` | `VARCHAR(255)` | 否 | `NULL` | 银行卡号密文 |
| `bank_card_hash` | `VARCHAR(64)` | 否 | `NULL` | 银行卡号 SHA-256 哈希 |
| `department` | `VARCHAR(50)` | 否 | `NULL` | 部门编码 |
| `position` | `VARCHAR(50)` | 否 | `NULL` | 岗位编码 |
| `rank` | `VARCHAR(20)` | 否 | `NULL` | 职级编码 |
| `salary` | `DECIMAL(10,2)` | 否 | `NULL` | 月薪/基础薪资 |
| `hire_date` | `DATE` | 否 | `NULL` | 入职日期 |
| `avatar` | `VARCHAR(255)` | 否 | `NULL` | 头像 URL |
| `status` | `TINYINT` | 否 | `1` | 状态：`0` 离职，`1` 在职 |
| `sort_order` | `INT` | 否 | `0` | 自定义排序号 |
| `emergency_contact` | `VARCHAR(50)` | 否 | `NULL` | 紧急联系人 |
| `emergency_phone` | `VARCHAR(20)` | 否 | `NULL` | 紧急联系电话 |
| `current_address` | `VARCHAR(255)` | 否 | `NULL` | 现住址 |
| `hukou_address` | `VARCHAR(255)` | 否 | `NULL` | 户籍地址 |
| `political_status` | `VARCHAR(20)` | 否 | `NULL` | 政治面貌 |
| `marital_status` | `TINYINT` | 否 | `0` | 婚姻状况：`0` 未婚，`1` 已婚，`2` 离异，`3` 丧偶 |
| `nation` | `VARCHAR(20)` | 否 | `NULL` | 民族 |
| `native_place` | `VARCHAR(50)` | 否 | `NULL` | 籍贯 |
| `deleted` | `TINYINT` | 否 | `0` | 逻辑删除 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：

| 索引名 | 类型 | 字段 | 说明 |
|---|---|---|---|
| `PRIMARY` | 主键 | `id` | 主键查询 |
| `uk_employee_no` | 唯一 | `employee_no` | 工号唯一 |
| `uk_phone` | 唯一 | `phone` | 手机号唯一 |
| `idx_name` | 普通 | `name` | 姓名模糊查询辅助 |
| `idx_department` | 普通 | `department` | 部门筛选与统计 |
| `idx_position` | 普通 | `position` | 岗位筛选与统计 |
| `idx_rank` | 普通 | `rank` | 职级筛选与统计 |
| `idx_id_card_hash` | 普通 | `id_card_hash` | 身份证号哈希校验查询 |
| `idx_bank_card_hash` | 普通 | `bank_card_hash` | 银行卡号哈希校验查询 |

业务规则：

- 新增员工时若未传入 `employee_no`，自动取当前最大工号加 1 并格式化为 4 位。
- 新增员工时若未传入 `sort_order`，自动取当前最大排序号加 1。
- `create_time`、`update_time`、`employee_no`、`deleted` 不应在普通更新接口中被修改。
- 员工列表默认排序：`sort_order ASC, create_time DESC`。

### 5.2 `employee_education` 员工教育经历表

用途：记录员工学历和教育经历，一个员工可拥有多条教育记录。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `school` | `VARCHAR(100)` | 是 | - | 学校名称 |
| `major` | `VARCHAR(100)` | 否 | `NULL` | 专业 |
| `education` | `VARCHAR(20)` | 否 | `NULL` | 学历：高中/大专/本科/硕士/博士 |
| `degree` | `VARCHAR(20)` | 否 | `NULL` | 学位：无/学士/硕士/博士 |
| `start_date` | `DATE` | 否 | `NULL` | 入学日期 |
| `end_date` | `DATE` | 否 | `NULL` | 毕业日期 |
| `is_full_time` | `TINYINT` | 否 | `1` | 是否全日制：`0` 否，`1` 是 |
| `sort_order` | `INT` | 否 | `0` | 排序 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`idx_employee_id(employee_id)`。

### 5.3 `employee_work_experience` 员工工作经历表

用途：记录员工入职前或历史工作经历。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `company` | `VARCHAR(100)` | 是 | - | 公司名称 |
| `position` | `VARCHAR(50)` | 否 | `NULL` | 职位 |
| `department` | `VARCHAR(50)` | 否 | `NULL` | 部门 |
| `start_date` | `DATE` | 否 | `NULL` | 入职日期 |
| `end_date` | `DATE` | 否 | `NULL` | 离职日期 |
| `leave_reason` | `VARCHAR(255)` | 否 | `NULL` | 离职原因 |
| `sort_order` | `INT` | 否 | `0` | 排序 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`idx_employee_id(employee_id)`。

### 5.4 `employee_family` 员工家庭成员表

用途：记录员工家庭成员及紧急联系以外的亲属信息。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `name` | `VARCHAR(50)` | 是 | - | 成员姓名 |
| `relation` | `VARCHAR(20)` | 是 | - | 与本人关系 |
| `gender` | `TINYINT` | 否 | `0` | 性别：`0` 女，`1` 男 |
| `birth_date` | `DATE` | 否 | `NULL` | 出生日期 |
| `company` | `VARCHAR(100)` | 否 | `NULL` | 工作单位 |
| `phone` | `VARCHAR(20)` | 否 | `NULL` | 联系电话 |
| `sort_order` | `INT` | 否 | `0` | 排序 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`idx_employee_id(employee_id)`。

### 5.5 `employee_certificate` 员工技能证书表

用途：记录员工职业资格、技能证书和有效期。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `name` | `VARCHAR(100)` | 是 | - | 证书名称 |
| `level` | `VARCHAR(20)` | 否 | `NULL` | 证书级别 |
| `issuer` | `VARCHAR(100)` | 否 | `NULL` | 发证机构 |
| `issue_date` | `DATE` | 否 | `NULL` | 发证日期 |
| `expire_date` | `DATE` | 否 | `NULL` | 到期日期 |
| `cert_no` | `VARCHAR(100)` | 否 | `NULL` | 证书编号 |
| `sort_order` | `INT` | 否 | `0` | 排序 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：

- `idx_employee_id(employee_id)`
- `idx_expire_date(expire_date)`，用于证书到期提醒或筛选。

### 5.6 `employee_contract` 员工合同信息表

用途：记录员工劳动合同、合同状态、合同薪资和附件。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `contract_no` | `VARCHAR(50)` | 否 | `NULL` | 合同编号 |
| `contract_type` | `VARCHAR(20)` | 是 | - | 合同类型 |
| `start_date` | `DATE` | 是 | - | 合同开始日期 |
| `end_date` | `DATE` | 否 | `NULL` | 合同结束日期，无固定期限可为空 |
| `probation_months` | `INT` | 否 | `0` | 试用期月数 |
| `probation_start_date` | `DATE` | 否 | `NULL` | 试用期开始日期 |
| `probation_end_date` | `DATE` | 否 | `NULL` | 试用期结束日期 |
| `signed_date` | `DATE` | 否 | `NULL` | 签订日期 |
| `salary` | `DECIMAL(10,2)` | 否 | `NULL` | 合同约定薪资 |
| `work_location` | `VARCHAR(100)` | 否 | `NULL` | 工作地点 |
| `status` | `TINYINT` | 否 | `1` | 合同状态 |
| `remark` | `VARCHAR(500)` | 否 | `NULL` | 备注 |
| `attachment_url` | `VARCHAR(255)` | 否 | `NULL` | 合同附件 URL |
| `sort_order` | `INT` | 否 | `0` | 排序 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

枚举：

- `contract_type`：固定期限、无固定期限、实习、兼职、劳务派遣。
- `status`：`0` 已到期，`1` 执行中，`2` 已终止，`3` 已续签。

索引：

- `idx_employee_id(employee_id)`
- `idx_end_date(end_date)`，用于合同到期提醒。
- `idx_status(status)`，用于合同状态筛选。

### 5.7 `employee_probation` 员工试用期记录表

用途：记录试用期起止、考核人、考核结果和延期信息。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `contract_id` | `BIGINT` | 否 | `NULL` | 关联合同 ID |
| `start_date` | `DATE` | 是 | - | 试用期开始日期 |
| `end_date` | `DATE` | 是 | - | 试用期结束日期 |
| `result` | `TINYINT` | 否 | `NULL` | 考核结果 |
| `result_date` | `DATE` | 否 | `NULL` | 考核结果日期 |
| `extension_end_date` | `DATE` | 否 | `NULL` | 延长后结束日期 |
| `evaluator` | `VARCHAR(50)` | 否 | `NULL` | 考核人 |
| `evaluation` | `TEXT` | 否 | `NULL` | 考核评语 |
| `remark` | `VARCHAR(500)` | 否 | `NULL` | 备注 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

枚举：

- `result`：`0` 未通过，`1` 通过提前转正，`2` 正常转正，`3` 延长试用期。

索引：

- `idx_employee_id(employee_id)`
- `idx_contract_id(contract_id)`
- `idx_end_date(end_date)`，用于试用期结束提醒。

### 5.8 `employee_workflow_change` 员工变更台账

用途：记录员工入转调离相关变更，包括调岗、调薪、转正和离职。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `change_type` | `VARCHAR(20)` | 是 | - | 变更类型 |
| `effective_date` | `DATE` | 是 | - | 生效日期 |
| `before_value` | `TEXT` | 否 | `NULL` | 变更前快照，JSON 文本 |
| `after_value` | `TEXT` | 否 | `NULL` | 变更后快照，JSON 文本 |
| `change_summary` | `VARCHAR(500)` | 否 | `NULL` | 变更摘要 |
| `status` | `TINYINT` | 否 | `1` | 状态：`0` 已撤销，`1` 已生效 |
| `applicant` | `VARCHAR(50)` | 否 | `NULL` | 申请人 |
| `apply_date` | `DATE` | 否 | `NULL` | 申请日期 |
| `approver` | `VARCHAR(50)` | 否 | `NULL` | 审批人 |
| `approve_date` | `DATE` | 否 | `NULL` | 审批日期 |
| `reason` | `VARCHAR(500)` | 否 | `NULL` | 变更原因 |
| `remark` | `VARCHAR(500)` | 否 | `NULL` | 备注 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

枚举：

- `change_type`：`TRANSFER` 调岗，`ADJUST_SALARY` 调薪，`CONFIRM` 转正，`LEAVE` 离职。

索引：

- `idx_employee_id(employee_id)`
- `idx_change_type(change_type)`
- `idx_effective_date(effective_date)`

### 5.9 `employee_leave` 员工离职记录

用途：记录离职申请、最后工作日、交接人、离职原因和社保公积金停缴时间。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `leave_type` | `VARCHAR(20)` | 否 | `主动离职` | 离职类型 |
| `apply_date` | `DATE` | 是 | - | 申请日期 |
| `last_work_date` | `DATE` | 是 | - | 最后工作日 |
| `leave_date` | `DATE` | 是 | - | 离职日期 |
| `handover_to` | `BIGINT` | 否 | `NULL` | 工作交接人，关联员工 ID |
| `handover_note` | `VARCHAR(1000)` | 否 | `NULL` | 交接说明 |
| `reason` | `VARCHAR(500)` | 否 | `NULL` | 离职原因 |
| `exit_interview_note` | `VARCHAR(1000)` | 否 | `NULL` | 离职面谈纪要 |
| `applicant` | `VARCHAR(50)` | 否 | `NULL` | 申请人 |
| `approver` | `VARCHAR(50)` | 否 | `NULL` | 审批人 |
| `approve_date` | `DATE` | 否 | `NULL` | 审批日期 |
| `social_insurance_cutoff` | `DATE` | 否 | `NULL` | 社保停缴月份 |
| `housing_fund_cutoff` | `DATE` | 否 | `NULL` | 公积金停缴月份 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

枚举：

- `leave_type`：主动离职、被动辞退、合同到期、协商解除。

索引：

- `idx_employee_id(employee_id)`
- `idx_leave_date(leave_date)`

### 5.10 `employee_attendance` 员工考勤记录表

用途：记录员工每天的签到签退、状态和工时。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `attendance_date` | `DATE` | 是 | - | 考勤日期 |
| `check_in_time` | `DATETIME` | 否 | `NULL` | 签到时间 |
| `check_out_time` | `DATETIME` | 否 | `NULL` | 签退时间 |
| `status` | `TINYINT` | 否 | `0` | 考勤状态 |
| `work_hours` | `DECIMAL(5,2)` | 否 | `NULL` | 工作时长，小时 |
| `remark` | `VARCHAR(500)` | 否 | `NULL` | 备注 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

枚举：

- `status`：`0` 正常，`1` 迟到，`2` 早退，`3` 缺勤，`4` 请假，`5` 出差。

索引：

| 索引名 | 类型 | 字段 | 说明 |
|---|---|---|---|
| `PRIMARY` | 主键 | `id` | 主键查询 |
| `uk_emp_date` | 唯一 | `employee_id, attendance_date` | 每个员工每天最多一条考勤 |
| `idx_attendance_date` | 普通 | `attendance_date` | 日期筛选 |

### 5.11 `salary_structure` 员工薪资结构表

用途：保存员工薪资模板，用于生成月度工资。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `base_salary` | `DECIMAL(10,2)` | 否 | `0` | 基本工资 |
| `performance_salary` | `DECIMAL(10,2)` | 否 | `0` | 绩效工资 |
| `allowance` | `DECIMAL(10,2)` | 否 | `0` | 岗位津贴 |
| `subsidy` | `DECIMAL(10,2)` | 否 | `0` | 补贴 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`idx_employee_id(employee_id)`。

### 5.12 `salary_record` 月度工资记录表

用途：记录员工每月工资明细、扣款、社保公积金个人部分、个税和发放状态。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `employee_id` | `BIGINT` | 是 | - | 员工 ID |
| `year_month` | `VARCHAR(7)` | 是 | - | 年月，格式 `YYYY-MM` |
| `base_salary` | `DECIMAL(10,2)` | 否 | `0` | 基本工资 |
| `performance_salary` | `DECIMAL(10,2)` | 否 | `0` | 绩效工资 |
| `allowance` | `DECIMAL(10,2)` | 否 | `0` | 岗位津贴 |
| `subsidy` | `DECIMAL(10,2)` | 否 | `0` | 补贴 |
| `overtime_pay` | `DECIMAL(10,2)` | 否 | `0` | 加班费 |
| `deduction` | `DECIMAL(10,2)` | 否 | `0` | 扣款 |
| `social_security_personal` | `DECIMAL(10,2)` | 否 | `0` | 社保个人部分 |
| `housing_fund_personal` | `DECIMAL(10,2)` | 否 | `0` | 公积金个人部分 |
| `tax` | `DECIMAL(10,2)` | 否 | `0` | 个人所得税 |
| `actual_salary` | `DECIMAL(10,2)` | 否 | `0` | 实发工资 |
| `status` | `TINYINT` | 否 | `0` | 工资状态 |
| `remark` | `VARCHAR(500)` | 否 | `NULL` | 备注 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

枚举：

- `status`：`0` 草稿，`1` 已确认，`2` 已发放。

索引：

| 索引名 | 类型 | 字段 | 说明 |
|---|---|---|---|
| `PRIMARY` | 主键 | `id` | 主键查询 |
| `uk_emp_month` | 唯一 | `employee_id, year_month` | 每个员工每月一条工资记录 |
| `idx_year_month` | 普通 | `year_month` | 月份查询与统计 |

工资计算口径：

```text
应发 = 基本工资 + 绩效工资 + 岗位津贴 + 补贴 + 加班费
扣减 = 扣款 + 社保个人部分 + 公积金个人部分 + 个税
实发 = 应发 - 扣减
```

个税计算使用中国七级超额累进税率，税率为 3% 到 45%，并结合速算扣除数。

### 5.13 `social_security_config` 社保公积金配置表

用途：按月份维护社保和公积金的缴费基数、公司比例和个人比例。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `year_month` | `VARCHAR(7)` | 是 | - | 年月，格式 `YYYY-MM` |
| `pension_base` | `DECIMAL(10,2)` | 否 | `0` | 养老保险基数 |
| `pension_company_rate` | `DECIMAL(5,2)` | 否 | `16` | 养老保险公司比例 |
| `pension_personal_rate` | `DECIMAL(5,2)` | 否 | `8` | 养老保险个人比例 |
| `medical_base` | `DECIMAL(10,2)` | 否 | `0` | 医疗保险基数 |
| `medical_company_rate` | `DECIMAL(5,2)` | 否 | `9.5` | 医疗保险公司比例 |
| `medical_personal_rate` | `DECIMAL(5,2)` | 否 | `2` | 医疗保险个人比例 |
| `unemployment_base` | `DECIMAL(10,2)` | 否 | `0` | 失业保险基数 |
| `unemployment_company_rate` | `DECIMAL(5,2)` | 否 | `0.5` | 失业保险公司比例 |
| `unemployment_personal_rate` | `DECIMAL(5,2)` | 否 | `0.5` | 失业保险个人比例 |
| `injury_base` | `DECIMAL(10,2)` | 否 | `0` | 工伤保险基数 |
| `injury_company_rate` | `DECIMAL(5,2)` | 否 | `0.4` | 工伤保险公司比例 |
| `maternity_base` | `DECIMAL(10,2)` | 否 | `0` | 生育保险基数 |
| `maternity_company_rate` | `DECIMAL(5,2)` | 否 | `0.8` | 生育保险公司比例 |
| `housing_fund_base` | `DECIMAL(10,2)` | 否 | `0` | 公积金基数 |
| `housing_fund_company_rate` | `DECIMAL(5,2)` | 否 | `12` | 公积金公司比例 |
| `housing_fund_personal_rate` | `DECIMAL(5,2)` | 否 | `12` | 公积金个人比例 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`uk_year_month(year_month)`，每月只允许一套配置。

### 5.14 `sys_department` 部门表

用途：维护组织架构部门树、负责人和部门状态。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `code` | `VARCHAR(50)` | 否 | `NULL` | 部门编码，唯一 |
| `name` | `VARCHAR(50)` | 是 | - | 部门名称 |
| `parent_code` | `VARCHAR(50)` | 否 | `NULL` | 父部门编码 |
| `leader_id` | `BIGINT` | 否 | `NULL` | 负责人员工 ID |
| `leader_name` | `VARCHAR(50)` | 否 | `NULL` | 负责人姓名快照 |
| `sort` | `INT` | 否 | `0` | 排序 |
| `status` | `TINYINT` | 否 | `1` | 状态：`0` 禁用，`1` 启用 |
| `description` | `VARCHAR(255)` | 否 | `NULL` | 部门说明 |
| `employee_count` | `INT` | 否 | `0` | 部门人数快照字段 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |
| `deleted` | `TINYINT` | 否 | `0` | 逻辑删除 |

索引：

- `PRIMARY(id)`
- `uk_code(code)`

注意：

- 当前业务查询部门人数时以 `employee` 表实时统计为准，`employee_count` 只作为兼容字段。
- 统计部门人数时应匹配 `employee.department = sys_department.code`，历史数据可兼容匹配部门名称。

### 5.15 `sys_dictionary` 系统字典表

用途：维护部门、岗位、职级等可配置枚举。当前组织设置中岗位和职级主要来源于该表。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `type` | `VARCHAR(50)` | 是 | - | 字典类型 |
| `code` | `VARCHAR(50)` | 否 | `NULL` | 字典编码 |
| `name` | `VARCHAR(50)` | 是 | - | 字典名称 |
| `sort` | `INT` | 否 | `0` | 排序 |
| `status` | `TINYINT` | 否 | `1` | 状态：`0` 禁用，`1` 启用 |
| `parent_code` | `VARCHAR(50)` | 否 | `NULL` | 父级编码 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`uk_type_name(type, name)`。

当前字典类型：

| 类型 | 说明 | 示例 |
|---|---|---|
| `department` | 部门 | `tech` 技术部 |
| `position` | 岗位 | `java_dev` Java开发 |
| `rank` | 职级 | `P5`、`P6`、`P7`、`P8` |

缓存策略：

- 字典查询使用 `@Cacheable`。
- 字典新增、更新、删除使用 `@CacheEvict` 清理字典缓存。

### 5.16 `sys_user` 系统用户表

用途：维护登录账号、角色和账号状态。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `username` | `VARCHAR(50)` | 是 | - | 用户名 |
| `password` | `VARCHAR(100)` | 是 | - | 密码哈希 |
| `nickname` | `VARCHAR(50)` | 否 | `NULL` | 昵称 |
| `role` | `VARCHAR(20)` | 否 | `USER` | 角色 |
| `status` | `TINYINT` | 否 | `1` | 状态：`0` 禁用，`1` 启用 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |
| `update_time` | `DATETIME` | 否 | 当前时间 | 更新时间 |

索引：`uk_username(username)`。

角色：

- `ADMIN`：系统管理员，可删除员工、维护字典。
- `USER`：普通用户。

### 5.17 `operation_log` 操作日志表

用途：记录用户操作，支持审计和字段变更追踪。

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---:|:---:|---|---|
| `id` | `BIGINT` | 是 | 自增 | 主键 |
| `module` | `VARCHAR(50)` | 否 | `NULL` | 操作模块 |
| `action` | `VARCHAR(50)` | 是 | - | 操作类型 |
| `content` | `VARCHAR(500)` | 否 | `NULL` | 操作内容 |
| `operator_id` | `BIGINT` | 否 | `NULL` | 操作人 ID |
| `operator_name` | `VARCHAR(50)` | 否 | `NULL` | 操作人姓名 |
| `ip` | `VARCHAR(50)` | 否 | `NULL` | IP 地址 |
| `create_time` | `DATETIME` | 否 | 当前时间 | 创建时间 |

索引：

- `idx_operator(operator_id)`
- `idx_create_time(create_time)`

查询维度：

- 模块：如员工、字典、薪资、考勤。
- 操作类型：新增、修改、删除、导入、导出、批量操作。
- 操作人。
- 时间范围。

## 6. 初始化数据

### 6.1 默认部门

| 编码 | 名称 | 排序 |
|---|---|---:|
| `tech` | 技术部 | 1 |
| `product` | 产品部 | 2 |
| `design` | 设计部 | 3 |
| `operation` | 运营部 | 4 |
| `hr` | 人事部 | 5 |
| `finance` | 财务部 | 6 |

### 6.2 默认岗位

| 编码 | 名称 | 所属部门 |
|---|---|---|
| `java_dev` | Java开发 | `tech` |
| `frontend_dev` | 前端开发 | `tech` |
| `product_manager` | 产品经理 | `product` |
| `ui_designer` | UI设计师 | `design` |
| `qa_engineer` | 测试工程师 | `tech` |
| `operation_specialist` | 运营专员 | `operation` |
| `hr_supervisor` | 人事主管 | `hr` |
| `finance_specialist` | 财务专员 | `finance` |

### 6.3 默认职级

| 编码 | 名称 | 排序 |
|---|---|---:|
| `P5` | P5 | 1 |
| `P6` | P6 | 2 |
| `P7` | P7 | 3 |
| `P8` | P8 | 4 |

### 6.4 默认账号

| 用户名 | 角色 | 说明 |
|---|---|---|
| `admin` | `ADMIN` | 系统管理员 |

## 7. 索引设计汇总

| 表 | 索引 | 类型 | 字段 | 设计目的 |
|---|---|---|---|---|
| `employee` | `uk_employee_no` | 唯一 | `employee_no` | 工号唯一 |
| `employee` | `uk_phone` | 唯一 | `phone` | 手机号唯一 |
| `employee` | `idx_name` | 普通 | `name` | 姓名查询 |
| `employee` | `idx_department` | 普通 | `department` | 部门筛选与统计 |
| `employee` | `idx_position` | 普通 | `position` | 岗位筛选与统计 |
| `employee` | `idx_rank` | 普通 | `rank` | 职级筛选与统计 |
| `employee` | `idx_id_card_hash` | 普通 | `id_card_hash` | 身份证号哈希唯一性校验查询 |
| `employee` | `idx_bank_card_hash` | 普通 | `bank_card_hash` | 银行卡号哈希唯一性校验查询 |
| `employee_attendance` | `uk_emp_date` | 唯一 | `employee_id, attendance_date` | 每日考勤唯一 |
| `employee_attendance` | `idx_attendance_date` | 普通 | `attendance_date` | 日期查询 |
| `salary_record` | `uk_emp_month` | 唯一 | `employee_id, year_month` | 每月工资唯一 |
| `salary_record` | `idx_year_month` | 普通 | `year_month` | 月份查询 |
| `social_security_config` | `uk_year_month` | 唯一 | `year_month` | 月度配置唯一 |
| `sys_department` | `uk_code` | 唯一 | `code` | 部门编码唯一 |
| `sys_department` | `idx_parent_code` | 普通 | `parent_code` | 组织树查询 |
| `sys_dictionary` | `uk_type_name` | 唯一 | `type, name` | 同类型字典名称唯一 |
| `sys_user` | `uk_username` | 唯一 | `username` | 用户名唯一 |
| `operation_log` | `idx_operator` | 普通 | `operator_id` | 操作人查询 |
| `operation_log` | `idx_create_time` | 普通 | `create_time` | 时间范围查询 |

## 8. 数据安全与权限

### 8.1 数据安全

- 身份证号、银行卡号入库前加密。
- 身份证号、银行卡号哈希只用于唯一性判断，不用于展示。
- 手机、邮箱、身份证号、银行卡号在前端展示时需脱敏。
- 文件上传需校验类型、大小、内容，并要求访问认证。

### 8.2 权限控制

- 只有 `ADMIN` 角色可以删除员工。
- 只有 `ADMIN` 角色可以维护字典和系统设置。
- 登录失败限制为 5 次，超过后锁定 30 分钟。
- API 限流默认 60 次/分钟。

## 9. 统计与缓存设计

### 9.1 统计口径

员工统计默认过滤：

```sql
deleted = 0
```

在职员工统计增加：

```sql
status = 1
```

部门人数建议实时统计：

```sql
SELECT COUNT(*)
FROM employee e
WHERE e.deleted = 0
  AND e.status = 1
  AND (e.department = :departmentCode OR e.department = :departmentName);
```

### 9.2 缓存策略

| 缓存内容 | 缓存名 | 默认 TTL | 失效时机 |
|---|---|---:|---|
| 字典数据 | `dictionary` | 600 秒 | 字典新增、编辑、删除 |
| 统计数据 | `statistics` | 300 秒 | 员工新增、编辑、删除、调岗、调薪 |

缓存支持 Redis + Caffeine 混合模式。当 Redis 不可用时，业务应自动降级到本地缓存或内存实现，避免缓存故障影响主流程。

## 10. 约束与改进建议

### 10.1 当前约束

- 员工手机号通过唯一索引保证唯一。
- 员工工号通过唯一索引保证唯一。
- 员工身份证号、银行卡号通过应用层哈希校验保证唯一。
- 考勤通过 `employee_id + attendance_date` 保证员工每日唯一。
- 工资记录通过 `employee_id + year_month` 保证员工每月唯一。
- 社保公积金配置通过 `year_month` 保证每月唯一。

### 10.2 建议后续优化

- 为 `employee.id_card_hash`、`employee.bank_card_hash` 增加唯一索引，强化数据库级唯一约束。
- 为所有员工子表补充数据库外键，或在文档中明确“仅应用层维护关系”。
- 将 `employee.department` 历史中文名称数据彻底迁移为部门编码，减少兼容逻辑。
- 如果需要更强审计能力，可将 `operation_log.content` 从 `VARCHAR(500)` 调整为 `TEXT`，保存更完整的字段变更详情。

## 11. 初始化与维护脚本

本地初始化：

```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS ems_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root ems_db < src/main/resources/db/schema.sql
```

字典迁移：

```bash
mysql -u root ems_db < migrate_dict.sql
```

常用检查 SQL：

```sql
-- 检查员工部门编码是否存在未归一化数据
SELECT department, COUNT(*) AS count
FROM employee
WHERE deleted = 0
GROUP BY department
ORDER BY count DESC;

-- 检查每个部门在职人数
SELECT d.code, d.name, COUNT(e.id) AS active_count
FROM sys_department d
LEFT JOIN employee e
  ON (e.department = d.code OR e.department = d.name)
 AND e.deleted = 0
 AND e.status = 1
WHERE d.deleted = 0
GROUP BY d.code, d.name, d.sort
ORDER BY d.sort;

-- 检查重复身份证哈希
SELECT id_card_hash, COUNT(*) AS count
FROM employee
WHERE deleted = 0 AND id_card_hash IS NOT NULL
GROUP BY id_card_hash
HAVING COUNT(*) > 1;

-- 检查重复银行卡哈希
SELECT bank_card_hash, COUNT(*) AS count
FROM employee
WHERE deleted = 0 AND bank_card_hash IS NOT NULL
GROUP BY bank_card_hash
HAVING COUNT(*) > 1;
```
