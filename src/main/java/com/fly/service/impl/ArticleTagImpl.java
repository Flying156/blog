package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.entity.ArticleTag;
import com.fly.mapper.ArticleTagMapper;
import com.fly.service.ArticleTagService;
import org.springframework.stereotype.Service;


/**
 * @author Milk
 */
@Service
public class ArticleTagImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
