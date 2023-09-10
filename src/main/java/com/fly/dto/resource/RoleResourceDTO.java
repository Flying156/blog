package com.fly.dto.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色资源数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResourceDTO {

    /**
     * 资源 ID
     */
    private Integer id;

    /**
     * 资源名称
     */
    private String label;


    /**
     * 子资源
     */
    private List<RoleResourceDTO> children;
}
