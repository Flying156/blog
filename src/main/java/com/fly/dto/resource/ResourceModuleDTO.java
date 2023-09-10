package com.fly.dto.resource;

import com.fly.entity.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;



/**
 * 资源模块数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceModuleDTO {


    /**
     * 资源模块列表
     */
    private List<Resource> resourceModuleList;


    /**
     * 模块 ID 与模块下资源的映射
     */
    private Map<Integer, List<Resource>> moduleIdChildrenMap;
}
