package com.fly.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fly.dto.article.ArticleSearchDTO;
import com.fly.strategy.SearchStrategy;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fly.constant.GenericConst.*;
import static com.fly.constant.SearchConst.*;
import static com.fly.enums.ArticleStatusEnum.PUBLIC;

/**
 * ElasticSearch 搜索
 *
 * @author Milk
 */
@Component

public class ElasticSearchStrategyImpl extends SearchStrategy {

    private final ElasticsearchRestTemplate restTemplate;

    public ElasticSearchStrategyImpl(ElasticsearchRestTemplate elasticsearchRestTemplate){
        this.restTemplate = elasticsearchRestTemplate;
    }

    @Override
    protected List<ArticleSearchDTO> search(String keywords) {
        return restTemplate
                .search(getSearchQuery(keywords), ArticleSearchDTO.class)
                .stream()
                .map(searchContent -> {
                    ArticleSearchDTO articleSearchDTO = searchContent.getContent();
                    Map<String, List<String>> highlightFields = searchContent.getHighlightFields();
                    // 填充数据
                    List<String> highlightTitle = highlightFields.get(ARTICLE_TITLE);
                    if(CollectionUtils.isNotEmpty(highlightTitle)){
                        articleSearchDTO.setArticleContent(highlightTitle.get(ZERO));
                    }

                    List<String> highlightContent = highlightFields.get(ARTICLE_CONTENT);
                    if(CollectionUtils.isNotEmpty(highlightContent)){
                        articleSearchDTO.setArticleContent(highlightContent.get(ZERO));
                    }
                    return articleSearchDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 构造查询条件
     * must: 与       should: 或
     * mustnot: 非    filter: 过滤
     * MatchQuery: 做分词处理
     * termQuery: 对文本进行全匹配
     */

    public NativeSearchQuery getSearchQuery(String keywords){
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        // 创建条件构造器，查询内容或标题
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery(ARTICLE_TITLE, keywords))
                        .should(QueryBuilders.matchQuery(ARTICLE_CONTENT, keywords)))
                .must(QueryBuilders.termQuery(IS_DELETE, FALSE_OF_INT))
                .must(QueryBuilders.termQuery(STATUS, PUBLIC.getStatus()));
        searchQuery.withQuery(queryBuilder);
        // 对标题和内容进行高亮处理，对于文本添加标签
        HighlightBuilder.Field articleTitle = new HighlightBuilder.Field(ARTICLE_TITLE);
        articleTitle.preTags(PRE_TAG);
        articleTitle.postTags(POST_TAG);
        HighlightBuilder.Field articleContent = new HighlightBuilder.Field(ARTICLE_CONTENT);
        articleContent.preTags(PRE_TAG);
        articleContent.postTags(POST_TAG);
        // 文章内容高亮限制 200 字符
        articleContent.fragmentSize(TWO_HUNDRED);
        // 添加高亮进入查询条件中
        searchQuery.withHighlightFields(articleTitle, articleContent);
        return searchQuery.build();
    }
}
