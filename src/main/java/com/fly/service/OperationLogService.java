package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.operationLog.OperationLogDTO;
import com.fly.entity.OperationLog;

import java.util.List;

/**
 * @author Milk
 */
public interface OperationLogService extends IService<OperationLog> {
    /**
     * 后台查看列表
     * @param keywords 操作日志名称
     * @return 列表
     */
    PageDTO<OperationLogDTO> listOperationLogs(String keywords);

    /**
     * 批量删除操作日志
     * @param operationLogIdList 操作日志列表
     */
    void removeOperationLog(List<Integer> operationLogIdList);
}
