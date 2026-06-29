-- 请假申请管理表
CREATE TABLE IF NOT EXISTS leave_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '请假记录ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    leave_type VARCHAR(20) NOT NULL COMMENT '请假类型：年假、病假、事假、婚假、产假、陪产假、丧假、其他',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    days DECIMAL(4,1) NOT NULL COMMENT '请假天数',
    reason VARCHAR(500) COMMENT '请假事由',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审批，1=已批准，2=已拒绝，3=已撤销',
    approver_id BIGINT COMMENT '审批人ID',
    approve_remark VARCHAR(500) COMMENT '审批备注',
    approve_time DATETIME COMMENT '审批时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';
