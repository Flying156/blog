package com.fly.constant;

/**
 * Hexo 文章常量
 * @author Milk
 */
public abstract class HexoConst {

    /**
     * 标签前缀
     */
    public static final String TAG_PREFIX = "tags:";

    /**
     * 分类前缀
     */
    public static final String CATEGORY_PREFIX = "categories:";

    /**
     * 文章标题前缀
     */
    public static final String TITLE_PREFIX = "title:";

    /**
     * 日期时间前缀
     */
    public static final String DATE_PREFIX = "date:";

    /**
     * 标签或类别名称前缀
     */
    public static final String CATEGORY_OR_TAG_NAME_PREFIX = "-";

    /**
     * 初始标记
     */
    public static final Integer NORMAL_FLAG = 0;

    /**
     * 分类标记
     */
    public static final Integer CATEGORY_FLAG = 1;

    /**
     * 标签标记
     */
    public static final Integer TAG_FLAG = 2;

    /**
     * 最大分隔符计数
     */
    public static final Integer MAX_DELIMITER_COUNT = 2;

    /**
     * 分隔符
     */
    public static final String DELIMITER = "---";

    /**
     * LF 换行符
     */
    public static final String LINE_FEED = "\n";

}
