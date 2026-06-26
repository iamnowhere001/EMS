package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.dto.WorkflowRequestDTO;
import com.ems.entity.EmployeeWorkflowChange;

public interface EmployeeWorkflowService {
    /**
     * 提交并应用一个入转调离流程申请。
     * @return 变更台账记录
     */
    EmployeeWorkflowChange submit(WorkflowRequestDTO req);

    /**
     * 撤销（逻辑删除）一个变更记录
     */
    boolean revoke(Long changeId);

    IPage<EmployeeWorkflowChange> page(Integer page, Integer size, String changeType, String keyword);
}
