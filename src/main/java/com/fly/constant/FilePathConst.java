package com.fly.constant;

/**
 * 文件存储路径常量
 *
 * @author Milk
 */
public abstract class FilePathConst {

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 路径分隔符
     */
    public static final String SEPARATOR = "\\";

    // endregion

    // region 目录 URI

    /**
     * 文章图片目录
     */
    public static final String ARTICLE_DIR = "article" + SEPARATOR;

    /**
     * 头像目录
     */
    public static final String AVATAR_DIR = "avatar" + SEPARATOR;

    /**
     * 配置图片目录
     */
    public static final String CONFIG_DIR = "config" + SEPARATOR;

    /**
     * Markdown 文件目录
     */
    public static final String MARKDOWN_DIR = "markdown" + SEPARATOR;

    /**
     * 照片目录
     */
    public static final String PHOTO_DIR = "photo" + SEPARATOR;

    /**
     * 说说图片目录
     */
    public static final String TALK_DIR = "talks" + SEPARATOR;

    /**
     * 语音目录
     */
    public static final String VOICE_DIR = "voice" + SEPARATOR;

}
