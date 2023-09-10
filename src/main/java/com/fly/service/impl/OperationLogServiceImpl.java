package com.fly.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.operationLog.OperationLogDTO;
import com.fly.entity.OperationLog;
import com.fly.mapper.OperationLogMapper;
import com.fly.service.OperationLogService;
import com.fly.util.ConvertUtils;
import com.fly.util.PageUtils;
import com.fly.util.StrRegexUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Milk
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PageDTO<OperationLogDTO> listOperationLogs(String keywords) {
        IPage<OperationLog> page = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords), OperationLog::getOptModule, keywords)
                .or().like(StrRegexUtils.isNotBlank(keywords), OperationLog::getOptDesc, keywords)
                .orderByDesc(OperationLog::getId)
                .page(PageUtils.getPage());
        List<OperationLogDTO> operationLogDTOList = ConvertUtils.convertList(page.getRecords(), OperationLogDTO.class);
        return PageUtils.build(operationLogDTOList, page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeOperationLog(List<Integer> operationLogIdList) {
        removeBatchByIds(operationLogIdList);
    }
}
