package com.fly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章种类枚举
 *
 * @author Milk
 */
@Getter
@AllArgsConstructor
public enum ArticleTypeEnum {

    /**
     * 原创
     */
    ORIGINAL(1, "原创"),

    /**
     * 转载
     */
    REPRINTED(2, "转载"),

    /**
     * 翻译
     */
    TRANSLATION(3, "翻译");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;
}
