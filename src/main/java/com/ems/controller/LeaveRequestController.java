package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.AuthContext;
import com.ems.common.BusinessException;
import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import com.ems.entity.LeaveRequest;
import com.ems.entity.User;
import com.ems.service.LeaveRequestService;
import com.ems.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    private final LeaveRequestService leaveService;
    private final UserService userService;

    public LeaveRequestController(LeaveRequestService leaveService, UserService userService) {
        this.leaveService = leaveService;
        this.userService = userService;
    }

    @PostMapping("/submit")
    @RequiresPermission("leave:submit")
    public Result<String> submit(@RequestBody LeaveRequest leaveRequest) {
        leaveRequest.setEmployeeId(resolveEmployeeScope(leaveRequest.getEmployeeId()));
        boolean ok = leaveService.submit(leaveRequest);
        return ok ? Result.success("申请提交成功") : Result.error("申请提交失败");
    }

    @PostMapping("/approve")
    @RequiresPermission("leave:approve")
    public Result<String> approve(@RequestParam Long id,
                                 @RequestParam Long approverId,
                                 @RequestParam(required = false) String remark) {
        boolean ok = leaveService.approve(id, approverId, remark);
        return ok ? Result.success("审批通过") : Result.error("审批失败");
    }

    @PostMapping("/reject")
    @RequiresPermission("leave:reject")
    public Result<String> reject(@RequestParam Long id,
                                @RequestParam Long approverId,
                                @RequestParam(required = false) String remark) {
        boolean ok = leaveService.reject(id, approverId, remark);
        return ok ? Result.success("已拒绝") : Result.error("操作失败");
    }

    @PostMapping("/cancel/{id}")
    @RequiresPermission("leave:cancel")
    public Result<String> cancel(@PathVariable Long id,
                                @RequestParam Long employeeId) {
        employeeId = resolveEmployeeScope(employeeId);
        boolean ok = leaveService.cancel(id, employeeId);
        return ok ? Result.success("已撤销") : Result.error("撤销失败，仅待审批状态可撤销");
    }

    @GetMapping("/page")
    @RequiresPermission("leave:view")
    public Result<IPage<LeaveRequest>> page(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        employeeId = resolveEmployeeScope(employeeId);
        return Result.success(leaveService.pageQuery(employeeId, status, month, page, size));
    }

    @GetMapping("/my")
    @RequiresPermission("leave:view")
    public Result<IPage<LeaveRequest>> my(
            @RequestParam Long employeeId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        employeeId = resolveEmployeeScope(employeeId);
        return Result.success(leaveService.pageQuery(employeeId, status, null, page, size));
    }

    @GetMapping("/statistics")
    @RequiresPermission("leave:view")
    public Result<List<Map<String, Object>>> statistics() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("status", 0); m1.put("label", "待审批"); m1.put("count", leaveService.countByStatus(0));
        Map<String, Object> m2 = new HashMap<>();
        m2.put("status", 1); m2.put("label", "已批准"); m2.put("count", leaveService.countByStatus(1));
        Map<String, Object> m3 = new HashMap<>();
        m3.put("status", 2); m3.put("label", "已拒绝"); m3.put("count", leaveService.countByStatus(2));
        Map<String, Object> m4 = new HashMap<>();
        m4.put("status", 3); m4.put("label", "已撤销"); m4.put("count", leaveService.countByStatus(3));
        list.add(m1); list.add(m2); list.add(m3); list.add(m4);
        return Result.success(list);
    }

    private Long resolveEmployeeScope(Long requestedEmployeeId) {
        List<String> permissions = AuthContext.getPermissions();
        if (permissions != null && (permissions.contains("*") || permissions.contains("leave:approve") || permissions.contains("leave:reject"))) {
            return requestedEmployeeId;
        }
        User user = userService.getById(AuthContext.getUserId());
        if (user == null || user.getEmployeeId() == null) {
            throw new BusinessException(400, "当前账号未绑定员工档案");
        }
        if (requestedEmployeeId != null && !requestedEmployeeId.equals(user.getEmployeeId())) {
            throw new BusinessException(403, "只能访问本人的请假数据");
        }
        return user.getEmployeeId();
    }
}
