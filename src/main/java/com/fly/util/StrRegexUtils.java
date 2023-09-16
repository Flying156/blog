package com.fly.util;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static com.fly.constant.GenericConst.EMPTY_STR;

/**
 * 字符串和正则表达式工具类
 *
 * @author Milk
 */
public class StrRegexUtils {

    /**
     * {@link StrRegexUtils#filter(String)}
     * 方法的正则表达式（保留图片标签）
     */
    public static final String[] FILTER_REGEXPS = new String[]
            {"(?!<(img).*?>)<.*?>", "(onload(.*?)=)", "(onerror(.*?)=)"};

    /**
     * {@link StrRegexUtils#deleteTag(String)} 方法的正则表示式, 防止脚本注入
     */
    public static final String[] DELETE_TAG_REGEXPS = new String[]
            {       // 转义字符
                    "&.{2,6}?;",
                    // script 标签
                    "<\\s*?script[^>]*?>[\\s\\S]*?<\\s*?/\\s*?script\\s*?>",
                    // style 标签
                    "<\\s*?style[^>]*?>[\\s\\S]*?<\\s*?/\\s*?style\\s*?>"
            };

    /**
     * Email 正则表达式，来自 <a href="http://emailregex.com/">Email Regex</a>
     */
    private static final String EMAIL_REGEX
            = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01" +
            "-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0" +
            "c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]" +
            "*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4" +
            "][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\" +
            "x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";


    /**
     * Email 正则表达式的编译表示形式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * 敏感词配置
     */
    public static final SensitiveWordBs WORD_BS = SensitiveWordBs.newInstance()
            .ignoreCase(true)
            .ignoreWidth(true)
            .ignoreNumStyle(true)
            .ignoreChineseStyle(true)
            .ignoreEnglishStyle(true)
            .ignoreRepeat(false)
            .enableNumCheck(true)
            .enableEmailCheck(true)
            .enableUrlCheck(true)
            .enableWordCheck(true)
            .numCheckLen(8)
            .init();


    /**
     * 判断是否为 null, 空（''）, 空格
     * @param charSequence 字符串
     * @return 是否为空
     */
    public static boolean isBlank(@Nullable CharSequence charSequence){
        if(charSequence == null){
            return true;
        }
        int length = charSequence.length();
        if(length == 0){
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String filter(@NotNull String source){
        source = WORD_BS.replace(source);
        // 过滤标签
        for(String regex : FILTER_REGEXPS){
            source = source.replaceAll(regex, EMPTY_STR);
        }
        return deleteTag(source);
    }


    /**
     * 删除标签
     *
     * @param source 文本
     * @return 删除后的文本
     */
    @NotNull
    public static String deleteTag(@NotNull String source) {
        for (String regexp : DELETE_TAG_REGEXPS) {
            source = source.replaceAll(regexp, EMPTY_STR);
        }
        return source;
    }


    /**
     * 判断是否为 null, 空（''）, 空格
     * @param charSequence 字符串
     * @return 是否为空
     */
    public static boolean isNotBlank(@Nullable CharSequence charSequence){
        return !isBlank(charSequence);
    }



    /**
     * 判断email格式
     * @param email 邮箱
     * @return 邮箱是否合适
     */
    public static boolean checkEmail(@NotNull String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
