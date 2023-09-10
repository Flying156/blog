package com.fly.mapper;

import com.fly.dto.article.ArticleSearchDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch 文章数据的操作接口
 *
 * @author Milk
 */
@Repository
public interface ArticleRepository extends ElasticsearchRepository<ArticleSearchDTO, Integer> {
}
