package com.fly.vo.article;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预览文章类
 *
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePreviewVO {

    /**
     * 分类 ID
     */
    @Schema(description = "分类 ID")
    private Integer categoryId;

    /**
     * 标签 ID
     */
    @Schema(description = "标签 ID")
    private Integer tagId;
}
