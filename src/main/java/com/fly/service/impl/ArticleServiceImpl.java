package com.fly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.constant.FilePathConst;
import com.fly.constant.GenericConst;
import com.fly.dto.article.*;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.entity.*;
import com.fly.enums.FileExtensionEnum;
import com.fly.mapper.ArticleMapper;
import com.fly.mapper.ArticleTagMapper;
import com.fly.mapper.CategoryMapper;
import com.fly.mapper.TagMapper;
import com.fly.service.ArticleService;
import com.fly.service.ArticleTagService;
import com.fly.service.TagService;
import com.fly.strategy.ImportStrategy;
import com.fly.strategy.SearchStrategy;
import com.fly.strategy.UploadStrategy;
import com.fly.util.*;
import com.fly.vo.article.*;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fly.constant.DatabaseConst.LIMIT_1;
import static com.fly.constant.DatabaseConst.LIMIT_5;
import static com.fly.constant.GenericConst.*;
import static com.fly.constant.RedisConst.*;
import static com.fly.constant.WebsiteConst.ARTICLE_SET;
import static com.fly.enums.ArticleStatusEnum.DRAFT;
import static com.fly.enums.ArticleStatusEnum.PUBLIC;

/**
 * @author Milk
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleTagService articleTagService;

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagService tagService;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private QiNiuUtils qiNiuUtils;

    @Resource
    private SearchStrategy searchStrategy;

    @Resource
    private UploadStrategy uploadStrategy;

    @Resource
    private ImportStrategy importStrategy;

    @Override
    public PageDTO<ArticleMangeDTO> listAdminArticles(ArticleSearchVO articleSearchVO) {
        // 查询符合条件的文章数
        Long articleCount = baseMapper.articleCount(articleSearchVO);

        if(articleCount.equals(ZERO_L)){
            return new PageDTO<>();
        }
        // 文章点赞
        Map<String, Object> likeCountMap = RedisUtils.hGetAll(ARTICLE_LIKE_COUNT);
        // 文章浏览量
        Map<Object, Double> articleIdViewsCountMap = RedisUtils
                .zRangeWithScores(ARTICLE_VIEWS_COUNT, ZERO, -ONE);

        long offset = PageUtils.offset(), size = PageUtils.size();
        CompletableFuture<List<ArticleMangeDTO>> future = AsyncUtils.supplyAsync
                (() -> baseMapper.listAdminArticles(offset, size, articleSearchVO));

        List<ArticleMangeDTO> articleMangeList = AsyncUtils.get
                (future, "获取文章列表");
        // 封装数据
        for(ArticleMangeDTO article : articleMangeList){
            Integer articleId = article.getId();
            Double viewCount = articleIdViewsCountMap.get(articleId);
            if(viewCount != null) {
                article.setViewsCount(articleIdViewsCountMap.get(articleId).intValue());
            }
            article.setLikeCount((Integer)likeCountMap.get(articleId.toString()));
        }

        return PageUtils.build(articleMangeList, articleCount);
    }

    @Override
    public void removeArticlePhysically(List<Integer> articleIdList) {
        articleTagService.lambdaUpdate()
                        .in(ArticleTag::getArticleId, articleIdList).remove();
        RedisUtils.del(TAG_PATTERN);
        RedisUtils.del(CATEGORY_PATTERN);
        baseMapper.deleteBatchIds(articleIdList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void recoverOrRemoveArticleLogically(TableLogicVO tableLogicVO) {
        Integer isDelete = tableLogicVO.getIsDelete();
        List<Article> articleList = tableLogicVO.getIdList()
                .stream().map(articleId ->
                          Article.builder()
                            .isDelete(isDelete)
                            .id(articleId)
                            .build()
                ).collect(Collectors.toList());
        updateBatchById(articleList);
    }

    @Override
    public void updateArticleTop(ArticleTopVO articleTopVO) {
        Article article = Article.builder()
                .id(articleTopVO.getId())
                .isTop(articleTopVO.getIsTop())
                .build();
        updateById(article);
    }

    @Override
    public ArticleVO getAdminArticle(Integer articleId) {
        Article article = baseMapper.selectById(articleId);
        ArticleVO articleVO = BeanCopyUtils.copy(article, ArticleVO.class);

        // 获得分类数据
        Category category = categoryMapper.selectById(article.getCategoryId());
        if(category != null){
            articleVO.setCategoryName(category.getCategoryName());
        }
        // 获取标签数据
        List<String> tagNameList = tagService.listArticleTagNames(articleId);
        articleVO.setTagNameList(tagNameList);
        return articleVO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        Article article = BeanCopyUtils.copy(articleVO, Article.class);
        // 获取当前登录用户 ID
        article.setUserId(SecurityUtils.getUserInfoId());
        // 获取文章分类，草稿无分类
        Category category = getCategory(articleVO);
        if (category != null) {
            boolean exists = new LambdaQueryChainWrapper<>(categoryMapper)
                    .eq(StrRegexUtils.isNotBlank(category.getCategoryName()), Category::getCategoryName, category.getCategoryName())
                            .exists();
            // 不存在则添加
            if(!exists){
                categoryMapper.insert(category);
                RedisUtils.del(CATEGORY_PATTERN);
            }
            article.setCategoryId(category.getId());
        }
        // 文章无封面则设置默认封面
        if(StrRegexUtils.isBlank(article.getArticleCover())){
            String articleCover = ConfigUtils.getCache(WebsiteConfig::getArticleCover);
            article.setArticleCover(articleCover);
        }
        // 保存或更改文章
        saveOrUpdate(article);
        // 还需要保存或更新文章和标签的映射
        saveOrUpdateArticleTagMap(articleVO, article.getId());

    }

    @Override
    public List<HomePageArticleDTO> listHomePageArticles() {
        return baseMapper.listHomePageArticles(PageUtils.offset(), PageUtils.size());
    }

    @Override
    public String saveArticleCover(MultipartFile multipartFile){
        return qiNiuUtils.uploadImage(multipartFile);
    }

    @Override
    public ArticlePreviewDTO getArticlePreview(ArticlePreviewVO articlePreviewVO) {
        // 获取分类或标签文章预览数据
        long offset = PageUtils.offset(), size = PageUtils.size();
        CompletableFuture<List<PreviewDTO>> previewFuture = AsyncUtils.supplyAsync(
                () -> baseMapper.listPreviewArticle(offset, size, articlePreviewVO));
        // 标签名或分类名
        String name = Optional.ofNullable(articlePreviewVO.getCategoryId())
                .map(categoryId -> categoryMapper.
                        selectById(categoryId).getCategoryName())
                .orElseGet(() ->
                    tagMapper.selectById(articlePreviewVO.getTagId())
                            .getTagName()
                );
        List<PreviewDTO> previewDTOList = AsyncUtils.get
                (previewFuture, "获取分类或标签文章预览数据异常");
        return new ArticlePreviewDTO(previewDTOList, name);
    }

    @Override
    public ArticleDTO getArticle(Integer articleId) {
        // 根据 ID 查询文章数据
        ArticleDTO articleDTO = baseMapper.getArticle(articleId);
        // 查询文章是否存在
        if(articleDTO == null){
            Integer userAuthId = null;
            try{
                userAuthId = SecurityUtils.getInfo(UserDetail::getId);
            }catch (ServiceException ignored){
                // 忽略用户未登录的情况
            }
            log.warn("Request to view non-existent article, articleId: {}, userAuthId: {}", articleId, userAuthId);
            throw new ServiceException("文章不存在，可能已被博主删除");
        }
        // 新增浏览量
        HttpSession session = WebUtils.getCurrentSession();
        AsyncUtils.runAsync(() -> updateArticleViewCount(session, articleId));
        CompletableFuture<List<ArticleRecommendDTO> > recommendFuture = AsyncUtils
                .supplyAsync(() -> baseMapper.listArticleRecommendArticles(articleId));
        // 最新的 5 篇文章
        CompletableFuture<List<ArticleRecommendDTO>> newestFuture = AsyncUtils.supplyAsync(
                () -> {
                    List<Article> articleList = lambdaQuery()
                            .select(Article::getId,
                                    Article::getArticleTitle,
                                    Article::getArticleCover,
                                    Article::getCreateTime)
                            .eq(Article::getStatus, PUBLIC.getStatus())
                            .eq(Article::getIsDelete, FALSE_OF_INT)
                            .orderByDesc(Article::getId)
                            .last(LIMIT_5).list();
                    return ConvertUtils.convertList(articleList, ArticleRecommendDTO.class);
                }
        );
        // 查询上一篇文章
        Article previousArticle = lambdaQuery()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .lt(Article::getId, articleId)
                .orderByDesc(Article::getId)
                .last(LIMIT_1).one();
        // 查询下一篇文章
        Article nextArticle = lambdaQuery()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .gt(Article::getId, articleId)
                .orderByDesc(Article::getId)
                .last(LIMIT_1).one();

        articleDTO.setLastArticle(BeanCopyUtils.copy(previousArticle, ArticlePaginationDTO.class));
        articleDTO.setNextArticle(BeanCopyUtils.copy(nextArticle, ArticlePaginationDTO.class));
        // 查询点赞数和浏览量
        Integer viewsCount = Optional
                .ofNullable(RedisUtils.zScore(ARTICLE_VIEWS_COUNT, articleId))
                .map(Double::intValue).orElse(ZERO);
        Integer likesCount = Optional
                .ofNullable((Integer) RedisUtils.hGet
                        (ARTICLE_LIKE_COUNT, articleId.toString()))
                .orElse(ZERO);
        articleDTO.setViewsCount(viewsCount);
        articleDTO.setLikeCount(likesCount);

        // 获取文章推荐和最新的文章列表
        List<ArticleRecommendDTO> newestArticleList = AsyncUtils.get(newestFuture, "查询 5 篇最新文章异常");
        List<ArticleRecommendDTO> recommendDTOList = AsyncUtils.get(recommendFuture, "推荐 6 篇文章异常");
        articleDTO.setRecommendArticleList(recommendDTOList);
        articleDTO.setNewestArticleList(newestArticleList);

        return articleDTO;
    }

    @Override
    public PageDTO<ArticleArchiveDTO> getArticleArchives() {
        IPage<Article> page = lambdaQuery()
                .select(Article::getId, Article::getArticleTitle, Article::getCreateTime)
                .eq(Article::getIsDelete, GenericConst.FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .orderByDesc(Article::getCreateTime)
                .page(PageUtils.getPage());
        return PageUtils.build(ConvertUtils.convertList(page.getRecords(), ArticleArchiveDTO.class), page.getTotal());
    }

    @Override
    public void likeArticle(Integer articleId) {
        // 需要判断是否是点赞还是取消点赞
        RedisUtils.likeOrUnlike(ARTICLE_LIKE_PREFIX, ARTICLE_LIKE_COUNT, articleId);
    }

    @Override
    public List<ArticleSearchDTO> listArticlesBySearch(String keywords) {
        return searchStrategy.searchArticle(keywords);
    }

    @Override
    public List<String> exportArticles(List<Integer> articleIdList) {
        return lambdaQuery()
                .select(Article::getArticleTitle, Article::getArticleContent)
                .in(Article::getId, articleIdList)
                .list().stream()
                .map(article -> {
                    byte [] contentBytes = article.getArticleContent().getBytes();
                    String fileName = article.getArticleTitle() + FilePathConst.DOT
                            + FileExtensionEnum.MD.getExtensionName();
                    try(ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes)){
                        return uploadStrategy.uploadFile(inputStream, FilePathConst.MARKDOWN_DIR, fileName);
                    }catch (IOException cause){
                        throw new ServiceException("文章导出失败", cause);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void importArticle(MultipartFile multipartFile, String strategyName) {
        // 为了解决循环依赖，不可将 Service 注入到此类中
        ArticleVO articleVO = importStrategy.importArticle(multipartFile, strategyName);
        saveOrUpdateArticle(articleVO);
    }

    /**
     * 保存或更新文章和标签的映射
     * @param articleVO 文章的标签
     * @param articleId 文章的ID
     */
    private void saveOrUpdateArticleTagMap(ArticleVO articleVO, Integer articleId) {
        //首先移除所有关于这个Article 和 Tag之间的关系
        LambdaQueryWrapper<ArticleTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleTag::getArticleId, articleId);
        articleTagMapper.delete(lambdaQueryWrapper);

        // 请求携带的标签名
        List<String> tagNameList = articleVO.getTagNameList();
        // 如果标签列表存在标签名
        if(CollectionUtils.isNotEmpty(tagNameList)){
             List<ArticleTag> articleTagList = tagNameList.stream()
                            .map(tagName -> {
                                Tag tag = new LambdaQueryChainWrapper<>(tagMapper)
                                        .eq(StrRegexUtils.isNotBlank(tagName), Tag::getTagName, tagName)
                                        .one();
                                // 新标签不存在需要添加

                                if(tag == null){
                                    tag = Tag.builder().tagName(tagName).build();
                                    tagMapper.insert(tag);
                                    // 删除缓存
                                    RedisUtils.del(TAG_PATTERN);
                                }
                                System.out.println(tag);
                                return ArticleTag.builder()
                                        .tagId(tag.getId())
                                        .articleId(articleId)
                                        .build();
                            }).collect(Collectors.toList());
            articleTagService.saveOrUpdateBatch(articleTagList);
        }
    }

    /**
     * 获取文章分类
     *
     * @param articleVO 文章数据
     * @return 文章分类，不存在分类的草稿返回 null
     */
    private Category getCategory(ArticleVO articleVO){
        Category category = new LambdaQueryChainWrapper<>(categoryMapper)
                .eq(Category::getCategoryName, articleVO.getCategoryName())
                .one();
        // 如果该分类不存在
        if(category == null){
            // 如果文章是草稿
            if (DRAFT.getStatus().equals(articleVO.getStatus())) {
                return null;
            }
            category = Category.builder()
                    .categoryName(articleVO.getCategoryName())
                    .build();
            categoryMapper.insert(category);
        }
        return category;
    }

    /**
     * 更新文章浏览量
     * @param session 当前的会话 session
     * @param articleId  文章 ID
     */
    private void updateArticleViewCount(HttpSession session, Integer articleId){
        // 一个用户只能增加一次访问量
        Set<Integer> articleSet = ConvertUtils.castSet
                (Optional.ofNullable(session.getAttribute(ARTICLE_SET))
                        .orElseGet(HashSet::new));
        // 如果当前会话没有访问过
        if(articleSet.add(articleId)) {
            session.setAttribute(ARTICLE_SET, articleSet);
            RedisUtils.zIncBy(ARTICLE_VIEWS_COUNT, articleId, ONE);
        }
    }
}
