package com.fly.dto.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台查看资源列表实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceMangeDTO {

    /**
     * 资源 ID
     */
    private Integer id;

    /**
     * 资源名
     */
    private String resourceName;

    /**
     * 权限 URI
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;
    // unused
    /**
     * 是否禁用
     */
    private Integer isDisable;

    /**
     * 是否匿名访问
     */
    private Integer isAnonymous;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子资源列表
     */
    private List<ResourceMangeDTO> children;

}
