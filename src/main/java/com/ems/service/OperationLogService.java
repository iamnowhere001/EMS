package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.OperationLog;

import java.time.LocalDateTime;

public interface OperationLogService extends IService<OperationLog> {

    /** 写入日志，自动从 AuthContext 读取操作人、IP、User-Agent。 */
    void log(String module, String action, String content);

    /** 显式指定操作人的日志（用于登录失败等尚未登录场景）。 */
    void logWithOperator(String operatorName, String ip, String module, String action, String content);

    IPage<OperationLog> pageQuery(Integer page, Integer size);

    IPage<OperationLog> pageQuery(Integer page, Integer size, String module, String action, String operator, LocalDateTime startTime, LocalDateTime endTime);
}
