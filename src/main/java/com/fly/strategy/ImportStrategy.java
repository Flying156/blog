package com.fly.strategy;

import com.fly.vo.article.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 导入策略类
 *
 * @author Milk
 */
public interface ImportStrategy {
    /**
     * 导入文章
     * @param multipartFile 文件
     * @param strategyName 普通文章还是 HEXO 文章
     */
    ArticleVO importArticle(MultipartFile multipartFile, String strategyName);
}
