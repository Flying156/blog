package com.fly.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章预览数据集
 *
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePreviewDTO {


    /**
     * 文章预览数据列表
     */
    private List<PreviewDTO> articlePreviewDTOList;

    /**
     * 标签名或分类名
     */
    private String name;
}
