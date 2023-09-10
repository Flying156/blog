package com.fly.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章搜索框查询分类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryOptionDTO {

    /**
     * 分类 ID
     */
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

}