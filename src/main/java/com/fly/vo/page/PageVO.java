package com.fly.vo.page;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO{

    /**
     * 页面 ID
     */
    @Schema(description = "页面 ID")
    private Integer id;

    /**
     * 页面名
     */
    @Schema(description = "页面名称")
    private String pageName;

    /**
     * 页面标签
     */
    @Schema(description = "页面标签")
    private String pageLabel;

    /**
     * 页面封面
     */
    @Schema(description = "页面封面")
    private String pageCover;
}
