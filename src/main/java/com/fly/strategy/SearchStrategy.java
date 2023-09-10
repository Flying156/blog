package com.fly.strategy;

import com.fly.dto.article.ArticleSearchDTO;
import com.fly.util.StrRegexUtils;
import net.dreamlu.mica.core.exception.ServiceException;

import java.util.LinkedList;
import java.util.List;

/**
 * 搜索策略类
 *
 * @author Milk
 */
public abstract class SearchStrategy {

    public List<ArticleSearchDTO> searchArticle(String keywords){
        if(StrRegexUtils.isBlank(keywords)){
            return new LinkedList<>();
        }
        try{
            return search(keywords);
        } catch (Exception cause){
            throw new ServiceException("搜索文章失败", cause);
        }
    }

    /**
     * 搜索文章数据
     * @param keywords 关键字
     */
    protected abstract List<ArticleSearchDTO> search(String keywords);
}
