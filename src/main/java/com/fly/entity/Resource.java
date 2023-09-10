package com.fly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_resource")
public class Resource {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 组件资源名称
     */
    private String resourceName;

    /**
     * 组件资源网址
     */
    private String url;

    /**
     * 请求方式 (GET, PUT ...)
     */
    private String requestMethod;


    /**
     * 父节点 ID
     */
    private Integer parentId;


    /**
     * 是否匿名访问（0 否 1 是）
     */
    private Integer isAnonymous;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
