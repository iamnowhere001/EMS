package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.LeaveRequest;

public interface LeaveRequestService extends IService<LeaveRequest> {

    IPage<LeaveRequest> pageQuery(Long employeeId, Integer status, String month, Integer page, Integer size);

    boolean submit(LeaveRequest leaveRequest);

    boolean approve(Long id, Long approverId, String approveRemark);

    boolean reject(Long id, Long approverId, String approveRemark);

    boolean cancel(Long id, Long employeeId);

    long countByStatus(Integer status);
}
