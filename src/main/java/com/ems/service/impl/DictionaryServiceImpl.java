package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.config.CacheConfig;
import com.ems.entity.Dictionary;
import com.ems.entity.Employee;
import com.ems.mapper.DictionaryMapper;
import com.ems.service.DictionaryService;
import com.ems.service.EmployeeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    private final EmployeeService employeeService;

    public DictionaryServiceImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_DICTIONARY, key = "#type + ':' + (#parentCode ?: 'ALL')", unless = "#result == null")
    public List<Dictionary> listByType(String type) {
        return listByType(type, null);
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_DICTIONARY, key = "#type + ':' + (#parentCode ?: 'ALL')", unless = "#result == null")
    public List<Dictionary> listByType(String type, String parentCode) {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dictionary::getType, type);
        wrapper.eq(Dictionary::getStatus, 1);
        wrapper.eq(StringUtils.hasText(parentCode), Dictionary::getParentCode, parentCode);
        wrapper.orderByAsc(Dictionary::getSort);
        return list(wrapper);
    }

    /**
     * 字典新增/更新/删除时清理缓存，保证数据一致性。
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_DICTIONARY, allEntries = true)
    })
    public boolean clearCacheAndSave(Dictionary dictionary) {
        return super.save(dictionary);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_DICTIONARY, allEntries = true)
    })
    public boolean clearCacheAndUpdate(Dictionary dictionary) {
        return super.updateById(dictionary);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_DICTIONARY, allEntries = true)
    })
    public boolean removeById(java.io.Serializable id) {
        Dictionary dict = getById(id);
        if (dict == null) {
            return false;
        }

        String type = dict.getType();
        String name = dict.getName();

        if ("department".equals(type)) {
            long count = employeeService.count(new LambdaQueryWrapper<Employee>()
                    .eq(Employee::getDepartment, name));
            if (count > 0) {
                throw new IllegalArgumentException("该部门下有 " + count + " 名员工，无法删除");
            }
        } else if ("position".equals(type)) {
            long count = employeeService.count(new LambdaQueryWrapper<Employee>()
                    .eq(Employee::getPosition, name));
            if (count > 0) {
                throw new IllegalArgumentException("该职位下有 " + count + " 名员工，无法删除");
            }
        }

        return super.removeById(id);
    }
}
