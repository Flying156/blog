package com.fly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体类
 * @author Milk
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_tag")
public class Tag {
    /**
     * 主键
     */
    @TableId(value = "id" , type = IdType.AUTO)
    private Integer id;
    /**
     * 标签名称
     */
    private String tagName;
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
