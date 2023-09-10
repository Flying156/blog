package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.operationLog.OperationLogDTO;
import com.fly.service.OperationLogService;
import com.fly.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.fly.enums.OperationLogEum.REMOVE;

/**
 * @author Milk
 */
@Tag(name = "操作日志模块")
@Validated
@RestController
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @Operation(summary = "后台查看操作日志")
    @GetMapping("/admin/operation/logs")
    public Result<PageDTO<OperationLogDTO>> viewOperationLogs(@RequestParam(required = false) String keywords){
        return Result.ok(operationLogService.listOperationLogs(keywords));
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/admin/operation/logs")
    public Result<?> removeOperationLog(@NotEmpty @RequestBody List<Integer> operationLogIdList){
        operationLogService.removeOperationLog(operationLogIdList);
        return Result.ok();
    }

}
