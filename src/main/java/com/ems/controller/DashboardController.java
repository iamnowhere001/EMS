package com.ems.controller;

import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import com.ems.service.DashboardService;
import com.ems.vo.DashboardVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    @RequiresPermission("dashboard:view")
    public Result<DashboardVO> overview() {
        return Result.success(dashboardService.loadDashboard());
    }
}
