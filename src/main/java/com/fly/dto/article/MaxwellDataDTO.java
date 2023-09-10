package com.fly.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Maxwell工具监听数据库实体类
 *
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxwellDataDTO {

    /**
     * 数据库
     */
    private String database;

    /**
     * xid
     */
    private Integer xid;

    /**
     * 数据
     */
    private Map<String, Object> data;

    /**
     * 是否提交
     */
    private Boolean commit;

    /**
     * 类型
     */
    private String type;

    /**
     * 表
     */
    private String table;

    /**
     * ts
     */
    private Integer ts;
}
