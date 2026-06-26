package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.SocialSecurityConfig;

public interface SocialSecurityConfigService extends IService<SocialSecurityConfig> {
    
    SocialSecurityConfig getByYearMonth(String yearMonth);
    
    boolean saveOrUpdateConfig(SocialSecurityConfig config);
}
