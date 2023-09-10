package com.fly.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台查询消息搜素类
 *
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageSearchVO {

    /**
     * 审核状态（ 0 否， 1 是）
     */
    @Schema(description = "是否审核")
    private Integer isReview;

    /**
     * 关键词
     */
    @Schema(description = "搜素关键词")
    private String keywords;

}
