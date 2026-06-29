package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.LeaveRequest;
import com.ems.mapper.LeaveRequestMapper;
import com.ems.service.LeaveRequestService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    @Override
    public IPage<LeaveRequest> pageQuery(Long employeeId, Integer status, String month, Integer page, Integer size) {
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(employeeId != null, LeaveRequest::getEmployeeId, employeeId);
        wrapper.eq(status != null, LeaveRequest::getStatus, status);
        if (StringUtils.hasText(month)) {
            wrapper.apply("DATE_FORMAT(start_date, '%Y-%m') = {0}", month);
        }
        wrapper.orderByDesc(LeaveRequest::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public boolean submit(LeaveRequest leaveRequest) {
        leaveRequest.setStatus(0);
        return this.save(leaveRequest);
    }

    @Override
    public boolean approve(Long id, Long approverId, String approveRemark) {
        LeaveRequest entity = new LeaveRequest();
        entity.setId(id);
        entity.setStatus(1);
        entity.setApproverId(approverId);
        entity.setApproveRemark(approveRemark);
        entity.setApproveTime(LocalDateTime.now());
        return this.updateById(entity);
    }

    @Override
    public boolean reject(Long id, Long approverId, String approveRemark) {
        LeaveRequest entity = new LeaveRequest();
        entity.setId(id);
        entity.setStatus(2);
        entity.setApproverId(approverId);
        entity.setApproveRemark(approveRemark);
        entity.setApproveTime(LocalDateTime.now());
        return this.updateById(entity);
    }

    @Override
    public boolean cancel(Long id, Long employeeId) {
        LeaveRequest existing = this.getById(id);
        if (existing == null || !existing.getEmployeeId().equals(employeeId)) {
            return false;
        }
        if (existing.getStatus() != 0) {
            return false;
        }
        LeaveRequest entity = new LeaveRequest();
        entity.setId(id);
        entity.setStatus(3);
        return this.updateById(entity);
    }

    @Override
    public long countByStatus(Integer status) {
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveRequest::getStatus, status);
        return this.count(wrapper);
    }
}
