package com.fly.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点和父节点的实体类
 *
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCountDTO {

    /**
     * 当前节点的数量
     */
    private Integer commentId;

    /**
     * 回复数
     */
    private Integer replyCount;

}
