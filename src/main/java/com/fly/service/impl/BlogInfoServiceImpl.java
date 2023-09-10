package com.fly.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fly.dto.article.ArticleRankDTO;
import com.fly.dto.article.DailyArticleDTO;
import com.fly.dto.blogInfo.AdminBlogInfoDTO;
import com.fly.dto.blogInfo.BlogInfoDTO;
import com.fly.dto.blogInfo.DailyVisitDTO;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.dto.category.CategoryDTO;
import com.fly.dto.tag.TagDTO;
import com.fly.entity.Article;
import com.fly.mapper.*;
import com.fly.schedule.VisitCountSchedule;
import com.fly.service.BlogInfoService;
import com.fly.service.PageService;
import com.fly.service.TagService;
import com.fly.util.*;
import com.fly.vo.bloginfo.InfoAboutMeVO;
import com.fly.vo.page.PageVO;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fly.constant.GenericConst.*;
import static com.fly.constant.RedisConst.*;
import static com.fly.enums.ArticleStatusEnum.PUBLIC;

/**
 * @author Milk
 */
@Service
@Slf4j
public class BlogInfoServiceImpl implements BlogInfoService {
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private TagService tagService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private PageService pageService;

    @Resource
    private QiNiuUtils qiNiuUtils;


    @Override
    public void updateVisitCount() {
        // 更新访客数量
        RedisUtils.incr(VISIT);
        RedisUtils.incr(DAILY_VISIT_PREFIX + TimeUtils.today());
        // 获取访客 IP 地址
        HttpServletRequest request = WebUtils.getCurrentRequest();
        String ipAddress = WebUtils.getIpAddress(request);
        // 获取访客浏览器，操作系统
        String browser = WebUtils.getBrowserName(request);
        String os = WebUtils.getOsName(request);
        // 统计省份访客量
        String visitorId = ipAddress + browser + os;
        String province = WebUtils.getInfo(ipAddress, IpInfo::getProvince);
        if(province != null){
            RedisUtils.sAdd(PROVINCE, province);
            RedisUtils.pfAdd(VISITOR_PROVINCE_PREFIX + province, visitorId);
        }
    }

    @Override
    public BlogInfoDTO getBlogInfo() {
        // 查询公开且并未被删除的文章的数量
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .count();
        // 查询分类数量
        Long categoryCount = categoryMapper.selectCount(null);
        // 查询标签数量
        Long tagCount = tagMapper.selectCount(null);
        // 查询网站配置
        WebsiteConfig websiteConfig = getWebSiteConfig();
        // 查询页面图片
        List<PageVO> pageList = pageService.listPages();
        // 查询访问量
        String viewsCount = Optional.ofNullable
                (RedisUtils.get(VISIT)).orElse(ZERO).toString();
        return BlogInfoDTO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .viewsCount(viewsCount)
                .websiteConfig(websiteConfig)
                .pageList(pageList)
                .build();
    }

    @Override
    public AdminBlogInfoDTO getAdminBlogInfo() {
        // 访问量前五的文章数据
        CompletableFuture<List<ArticleRankDTO>> rankFuture
                = AsyncUtils.supplyAsync(() -> {
           Map<Object, Double> articleIdScoreMap = RedisUtils
                   .zRevRangeWithScores(ARTICLE_VIEWS_COUNT, ZERO, FOUR);
           return buildArticleRank(articleIdScoreMap);
        });


        // 查询公开且并未被删除的文章的数量
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .count();
        // 查询用户数量
        Long userCount = userInfoMapper.selectCount(null);
        // 分类统计
        List<CategoryDTO> categoryDTOList = categoryMapper.listCategories();
        // 文章每日统计
        List<DailyArticleDTO> dailyArticleDTOList = articleMapper.listDailyArticles();
        // 留言数量
        Long messageCount = messageMapper.selectCount(null);
        // 标签数据
        List<TagDTO> tagDTOList = tagService.listAdminArticleTags();
        // 访问量计数
        Integer viewsCount = (Integer) Optional.ofNullable
                (RedisUtils.get(VISIT)).orElse(ZERO);
        // 每周的访问数据
        List<DailyVisitDTO> dailyVisitDTOList = VisitCountSchedule.getWeeklyVisit();
        // 排名前五的文章数据
        List<ArticleRankDTO> articleRankDTOList = AsyncUtils.get(rankFuture, "获取访问量前五文章数据异常");
        return AdminBlogInfoDTO.builder()
                .articleCount(articleCount)
                .userCount(userCount)
                .messageCount(messageCount)
                .categoryDTOList(categoryDTOList)
                .articleStatisticsList(dailyArticleDTOList)
                .tagDTOList(tagDTOList)
                .viewsCount(viewsCount)
                .dailyVisitDTOList(dailyVisitDTOList)
                .articleRankDTOList(articleRankDTOList)
                .build();
    }


    @Override
    public void updateWebSiteConfig(WebsiteConfig websiteConfig) {
        RedisUtils.set(WEBSITE_CONFIG, websiteConfig);
    }

    @Override
    public WebsiteConfig getWebSiteConfig() {
        WebsiteConfig websiteConfig = JSONObject.parseObject(JSONObject.toJSONString(RedisUtils.get(WEBSITE_CONFIG)), WebsiteConfig.class);
        if(websiteConfig == null){
            log.error("网站无配置信息，请前往后台管理配置");
            websiteConfig = new WebsiteConfig();
        }
        return websiteConfig;
    }

    @Override
    public String getAboutMe() {
        return Optional.ofNullable((String) RedisUtils.get(INFO_ABOUT_ME))
                .orElse(EMPTY_STR);
    }

    @Override
    public void updateAboutMe(InfoAboutMeVO infoAboutMeVO) {
        RedisUtils.set(INFO_ABOUT_ME, infoAboutMeVO.getAboutContent());
    }

    @Override
    public String uploadWebsiteImage(MultipartFile file) {
        return qiNiuUtils.uploadImage(file);
    }



    /**
     * 组装文章排行数据
     */
    private List<ArticleRankDTO> buildArticleRank(Map<Object, Double> articleIdScoreMap) {
        if(CollectionUtils.isEmpty(articleIdScoreMap)){
            return new ArrayList<>();
        }
        return new LambdaQueryChainWrapper<>(articleMapper)
                .select(Article::getId, Article::getArticleTitle)
                .in(Article::getId, articleIdScoreMap.keySet())
                .list().stream()
                .map(article -> ArticleRankDTO.builder()
                        .articleTitle(article.getArticleTitle())
                        .viewsCount(articleIdScoreMap.get(article.getId()).intValue())
                        .build())
                .collect(Collectors.toList());

    }
}
