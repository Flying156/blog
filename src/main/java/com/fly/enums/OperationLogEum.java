package com.fly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志枚举
 * @author Milk
 */
@Getter
@AllArgsConstructor
public enum OperationLogEum {


    /**
     * 删除
     */
    REMOVE("删除"),

    /**
     * 新增
     */
    SAVE("新增"),

    /**
     * 新增或修改
     */
    SAVE_OR_UPDATE("新增或修改"),

    /**
     * 修改
     */
    UPDATE("修改"),

    /**
     * 上传
     */
    UPLOAD("上传");

    /**
     * 描述值
     */
    private final String value;
}
