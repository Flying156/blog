package com.fly.dto.userAuth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计地域分布
 *
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaCountDTO {
    /**
     * 地区名
     */
    private String name;
    /**
     * 数量
     */
    private Long value;
}
