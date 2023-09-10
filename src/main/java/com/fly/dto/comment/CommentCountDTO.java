package com.fly.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论主题 ID 与评论数量数据
 *
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCountDTO {

    /**
     * 评论主题 ID
     */
    private Integer topicId;

    /**
     * 评论数量
     */
    private Integer commentCount;

}
