package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.SocialSecurityConfig;
import com.ems.mapper.SocialSecurityConfigMapper;
import com.ems.service.SocialSecurityConfigService;
import org.springframework.stereotype.Service;

@Service
public class SocialSecurityConfigServiceImpl extends ServiceImpl<SocialSecurityConfigMapper, SocialSecurityConfig> implements SocialSecurityConfigService {

    @Override
    public SocialSecurityConfig getByYearMonth(String yearMonth) {
        LambdaQueryWrapper<SocialSecurityConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SocialSecurityConfig::getYearMonth, yearMonth);
        return this.getOne(wrapper);
    }

    @Override
    public boolean saveOrUpdateConfig(SocialSecurityConfig config) {
        SocialSecurityConfig existing = getByYearMonth(config.getYearMonth());
        if (existing != null) {
            config.setId(existing.getId());
            return this.updateById(config);
        } else {
            return this.save(config);
        }
    }
}
