package com.fly.dto.blogInfo;


import com.fly.vo.page.PageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogInfoDTO {


    /**
     * 文章数量
     */
    private Long articleCount;
    /**
     * 分类数量
     */
    private Long categoryCount;
    /**
     * 标签数量
     */
    private Long tagCount;
    /**
     * 访问量
     */
    private String viewsCount;

    /**
     * 网站配置
     */
    private WebsiteConfig websiteConfig;

    /**
     * 页面列表
     */
    private List<PageVO> pageList;
}
