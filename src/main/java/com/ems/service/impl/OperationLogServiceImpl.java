package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.AuthContext;
import com.ems.entity.OperationLog;
import com.ems.mapper.OperationLogMapper;
import com.ems.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    /**
     * 异步写入日志，不影响主业务性能。
     */
    @Override
    @Async
    public void log(String module, String action, String content) {
        OperationLog log = new OperationLog();
        log.setModule(module);
        log.setAction(action);
        log.setContent(content);
        log.setOperatorId(AuthContext.getUserId());
        log.setOperatorName(AuthContext.getUsername());
        log.setIp(AuthContext.getIp());
        this.save(log);
    }

    /**
     * 用于登录失败、退出等尚未经过 LoginInterceptor 的场景。
     */
    @Override
    @Async
    public void logWithOperator(String operatorName, String ip, String module, String action, String content) {
        OperationLog log = new OperationLog();
        log.setModule(module);
        log.setAction(action);
        log.setContent(content);
        log.setOperatorName(operatorName);
        log.setIp(ip);
        this.save(log);
    }

    @Override
    public IPage<OperationLog> pageQuery(Integer page, Integer size) {
        return this.page(new Page<>(page, size),
                new LambdaQueryWrapper<OperationLog>()
                        .orderByDesc(OperationLog::getCreateTime));
    }

    @Override
    public IPage<OperationLog> pageQuery(Integer page, Integer size, String module, String action, String operator, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (action != null && !action.isEmpty()) {
            wrapper.eq(OperationLog::getAction, action);
        }
        if (operator != null && !operator.isEmpty()) {
            wrapper.like(OperationLog::getOperatorName, operator);
        }
        if (startTime != null) {
            wrapper.ge(OperationLog::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getCreateTime, endTime);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }
}
