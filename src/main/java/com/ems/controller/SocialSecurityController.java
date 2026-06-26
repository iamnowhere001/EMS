package com.ems.controller;

import com.ems.common.RequireRole;
import com.ems.common.Result;
import com.ems.entity.SocialSecurityConfig;
import com.ems.service.SocialSecurityConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/socialSecurity")
public class SocialSecurityController {

    private final SocialSecurityConfigService socialSecurityConfigService;

    public SocialSecurityController(SocialSecurityConfigService socialSecurityConfigService) {
        this.socialSecurityConfigService = socialSecurityConfigService;
    }

    @GetMapping
    public Result<SocialSecurityConfig> getByYearMonth(@RequestParam String yearMonth) {
        return Result.success(socialSecurityConfigService.getByYearMonth(yearMonth));
    }

    @PostMapping
    @RequireRole("admin")
    public Result<String> saveOrUpdate(@RequestBody SocialSecurityConfig config) {
        socialSecurityConfigService.saveOrUpdateConfig(config);
        return Result.success("保存成功");
    }
}
