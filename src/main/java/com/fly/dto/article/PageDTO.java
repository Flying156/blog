package com.fly.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

    /**
     * 总数
     */
    private Long count;

    /**
     * 数据列表
     */
    private List<T> recordList;

}
