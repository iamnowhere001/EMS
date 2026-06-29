package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.AuthContext;
import com.ems.common.BusinessException;
import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import com.ems.entity.EmployeeAttendance;
import com.ems.entity.User;
import com.ems.service.EmployeeAttendanceService;
import com.ems.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final EmployeeAttendanceService attendanceService;
    private final UserService userService;

    public AttendanceController(EmployeeAttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @GetMapping("/page")
    @RequiresPermission("attendance:view")
    public Result<IPage<EmployeeAttendance>> page(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        employeeId = resolveEmployeeScope(employeeId);
        return Result.success(attendanceService.pageQuery(employeeId, yearMonth, page, size));
    }

    @GetMapping("/list")
    @RequiresPermission("attendance:view")
    public Result<List<EmployeeAttendance>> list(
            @RequestParam Long employeeId,
            @RequestParam(required = false) String yearMonth) {
        employeeId = resolveEmployeeScope(employeeId);
        return Result.success(attendanceService.listByEmployeeId(employeeId, yearMonth));
    }

    @PostMapping("/checkIn")
    @RequiresPermission("attendance:checkin")
    public Result<String> checkIn(@RequestParam Long employeeId) {
        employeeId = resolveEmployeeScope(employeeId);
        boolean ok = attendanceService.checkIn(employeeId, LocalDate.now());
        return ok ? Result.success("签到成功") : Result.error("今日已签到");
    }

    @PostMapping("/checkOut")
    @RequiresPermission("attendance:checkin")
    public Result<String> checkOut(@RequestParam Long employeeId) {
        employeeId = resolveEmployeeScope(employeeId);
        boolean ok = attendanceService.checkOut(employeeId, LocalDate.now());
        return ok ? Result.success("签退成功") : Result.error("今日已签退");
    }

    @PostMapping("/batchImport")
    @RequiresPermission("attendance:manage")
    public Result<String> batchImport(@RequestBody List<EmployeeAttendance> attendanceList) {
        int count = attendanceService.batchImport(attendanceList);
        return Result.success("成功导入 " + count + " 条考勤记录");
    }

    @PutMapping
    @RequiresPermission("attendance:manage")
    public Result<String> update(@RequestBody EmployeeAttendance attendance) {
        attendanceService.updateById(attendance);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("attendance:manage")
    public Result<String> delete(@PathVariable Long id) {
        attendanceService.removeById(id);
        return Result.success("删除成功");
    }

    private Long resolveEmployeeScope(Long requestedEmployeeId) {
        List<String> permissions = AuthContext.getPermissions();
        if (permissions != null && (permissions.contains("*") || permissions.contains("attendance:manage"))) {
            return requestedEmployeeId;
        }
        User user = userService.getById(AuthContext.getUserId());
        if (user == null || user.getEmployeeId() == null) {
            throw new BusinessException(400, "当前账号未绑定员工档案");
        }
        if (requestedEmployeeId != null && !requestedEmployeeId.equals(user.getEmployeeId())) {
            throw new BusinessException(403, "只能访问本人的考勤数据");
        }
        return user.getEmployeeId();
    }
}
