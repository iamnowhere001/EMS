package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.Result;
import com.ems.entity.EmployeeAttendance;
import com.ems.service.EmployeeAttendanceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final EmployeeAttendanceService attendanceService;

    public AttendanceController(EmployeeAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/page")
    public Result<IPage<EmployeeAttendance>> page(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(attendanceService.pageQuery(employeeId, yearMonth, page, size));
    }

    @GetMapping("/list")
    public Result<List<EmployeeAttendance>> list(
            @RequestParam Long employeeId,
            @RequestParam(required = false) String yearMonth) {
        return Result.success(attendanceService.listByEmployeeId(employeeId, yearMonth));
    }

    @PostMapping("/checkIn")
    public Result<String> checkIn(@RequestParam Long employeeId) {
        boolean ok = attendanceService.checkIn(employeeId, LocalDate.now());
        return ok ? Result.success("签到成功") : Result.error("今日已签到");
    }

    @PostMapping("/checkOut")
    public Result<String> checkOut(@RequestParam Long employeeId) {
        boolean ok = attendanceService.checkOut(employeeId, LocalDate.now());
        return ok ? Result.success("签退成功") : Result.error("今日已签退");
    }

    @PostMapping("/batchImport")
    public Result<String> batchImport(@RequestBody List<EmployeeAttendance> attendanceList) {
        int count = attendanceService.batchImport(attendanceList);
        return Result.success("成功导入 " + count + " 条考勤记录");
    }

    @PutMapping
    public Result<String> update(@RequestBody EmployeeAttendance attendance) {
        attendanceService.updateById(attendance);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        attendanceService.removeById(id);
        return Result.success("删除成功");
    }
}
