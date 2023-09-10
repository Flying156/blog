package com.fly.constant;

/**
 * Redis 键常量
 * @author Milk
 */
public abstract class RedisConst {

    /**
     * 验证码过期时间（分钟）
     */
    public static final long CODE_EXPIRE_TIME = 15;


    /**
     * 在线用户名集合
     */
    public static final String ONLINE_USERNAME = "online_username";

    /**
     * 网站配置
     */
    public static final String WEBSITE_CONFIG = "website_config";
    /**
     * 关于我信息
     */
    public static final String INFO_ABOUT_ME = "info_about_me";

    /**
     * 验证码
     */
    public static final String CODE_PREFIX = "code:";
    /**
     * 用户地域
     */
    public static final String USER_AREA = "user_area";
    /**
     * 游客地域
     */
    public static final String VISITOR_AREA = "visitor_area";
    /**
     * 统计总访问量
     */
    public static final String VISIT = "visit";
    /**
     * 统计每日访问量
     */
    public static final String DAILY_VISIT_PREFIX = "daily_visit:";

    /**
     * 文章点赞量
     */
    public static final String ARTICLE_LIKE_COUNT = "article_like_count";

    /**
     * 文章浏览量
     */
    public static final String ARTICLE_VIEWS_COUNT = "article_views_count";

    /**
     * 省份名
     */
    public static final String PROVINCE = "province";

    /**
     * 统计省份访客量
     */
    public static final String VISITOR_PROVINCE_PREFIX = "visitor_province:";

    /**
     * 用户点赞文章
     */
    public static final String ARTICLE_LIKE_PREFIX = "article_like:";

    /**
     * 用户点赞评论
     */
    public static final String COMMENT_LIKE_PREFIX = "comment_like:";

    /**
     * 评论点赞量
     */
    public static final String COMMENT_LIKE_COUNT = "comment_like_count";

    /**
     * 用户点赞说说
     */
    public static final String TALK_LIKE_PREFIX = "talk_like:";

    /**
     * 说说点赞量
     */
    public static final String TALK_LIKE_COUNT = "talk_like_count";

    /**
     * 寻找包下的所有缓存
     */
    public static final String TAG_PATTERN = "cache:tag::*";

    /**
     * 寻找包下的所有缓存
     */
    public static final String CATEGORY_PATTERN = "cache:category::*";
}
