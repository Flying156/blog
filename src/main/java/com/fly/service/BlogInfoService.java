package com.fly.service;

import com.fly.dto.blogInfo.AdminBlogInfoDTO;
import com.fly.dto.blogInfo.BlogInfoDTO;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.vo.bloginfo.InfoAboutMeVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 博客信息服务
 * @author Milk
 */
public interface BlogInfoService {
    /**
     * 获得博客数据
     * @return 博客信息
     */
    BlogInfoDTO getBlogInfo();

    /**
     * 获得后台博客首页信息
     * @return 后台博客信息
     */
    AdminBlogInfoDTO getAdminBlogInfo();

    /**
     * 更新网站配置
     * @param websiteConfig 配置信息
     */
    void updateWebSiteConfig(WebsiteConfig websiteConfig);

    /**
     * 获取网站配置
     * @return 配置信息
     */
    WebsiteConfig getWebSiteConfig();

    /**
     * 前台得到关于我
     * @return 数据
     */
    String getAboutMe();

    /**
     * 更新关于我
     */
    void updateAboutMe(InfoAboutMeVO infoAboutMeVO);

    /**
     * 上传网站图片
     */
    String uploadWebsiteImage(MultipartFile file);

    /**
     * 统计访问量信息
     */
    void updateVisitCount();
}
