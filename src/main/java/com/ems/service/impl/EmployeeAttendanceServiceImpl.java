package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.EmployeeAttendance;
import com.ems.mapper.EmployeeAttendanceMapper;
import com.ems.service.EmployeeAttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class EmployeeAttendanceServiceImpl extends ServiceImpl<EmployeeAttendanceMapper, EmployeeAttendance> implements EmployeeAttendanceService {

    @Override
    public IPage<EmployeeAttendance> pageQuery(Long employeeId, String yearMonth, Integer page, Integer size) {
        LambdaQueryWrapper<EmployeeAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(employeeId != null, EmployeeAttendance::getEmployeeId, employeeId);
        if (StringUtils.hasText(yearMonth)) {
            wrapper.apply("DATE_FORMAT(attendance_date, '%Y-%m') = {0}", yearMonth);
        }
        wrapper.orderByDesc(EmployeeAttendance::getAttendanceDate);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<EmployeeAttendance> listByEmployeeId(Long employeeId, String yearMonth) {
        LambdaQueryWrapper<EmployeeAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeAttendance::getEmployeeId, employeeId);
        if (StringUtils.hasText(yearMonth)) {
            wrapper.apply("DATE_FORMAT(attendance_date, '%Y-%m') = {0}", yearMonth);
        }
        wrapper.orderByDesc(EmployeeAttendance::getAttendanceDate);
        return this.list(wrapper);
    }

    @Override
    public boolean checkIn(Long employeeId, LocalDate date) {
        EmployeeAttendance attendance = getOrCreate(employeeId, date);
        if (attendance.getCheckInTime() != null) {
            return false; // 已签到
        }
        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckInTime(now);
        
        // 判断是否迟到（假设 9:00 上班）
        LocalTime workStartTime = LocalTime.of(9, 0);
        if (now.toLocalTime().isAfter(workStartTime)) {
            attendance.setStatus(1); // 迟到
        } else {
            attendance.setStatus(0); // 正常
        }
        
        return this.updateById(attendance);
    }

    @Override
    public boolean checkOut(Long employeeId, LocalDate date) {
        EmployeeAttendance attendance = getOrCreate(employeeId, date);
        if (attendance.getCheckOutTime() != null) {
            return false; // 已签退
        }
        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckOutTime(now);
        
        // 计算工作时长
        if (attendance.getCheckInTime() != null) {
            long minutes = Duration.between(attendance.getCheckInTime(), now).toMinutes();
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
            attendance.setWorkHours(hours);
        }
        
        // 判断是否早退（假设 18:00 下班）
        LocalTime workEndTime = LocalTime.of(18, 0);
        if (now.toLocalTime().isBefore(workEndTime)) {
            attendance.setStatus(2); // 早退
        } else if (attendance.getStatus() == null || attendance.getStatus() == 0) {
            attendance.setStatus(0); // 正常
        }
        
        return this.updateById(attendance);
    }

    @Override
    public int batchImport(List<EmployeeAttendance> attendanceList) {
        if (attendanceList == null || attendanceList.isEmpty()) {
            return 0;
        }
        return this.saveBatch(attendanceList) ? attendanceList.size() : 0;
    }

    private EmployeeAttendance getOrCreate(Long employeeId, LocalDate date) {
        LambdaQueryWrapper<EmployeeAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeAttendance::getEmployeeId, employeeId)
               .eq(EmployeeAttendance::getAttendanceDate, date);
        EmployeeAttendance attendance = this.getOne(wrapper);
        if (attendance == null) {
            attendance = new EmployeeAttendance();
            attendance.setEmployeeId(employeeId);
            attendance.setAttendanceDate(date);
            this.save(attendance);
        }
        return attendance;
    }
}
