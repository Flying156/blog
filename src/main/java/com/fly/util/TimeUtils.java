package com.fly.util;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.fly.constant.TimeConst.*;

/**
 * 时间工具类
 *
 * @author Milk
 */
public abstract class TimeUtils {

    /**
     * 日期时间格式化
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    public static DateTimeFormatter getDateTimeFormatter(){
        return DATE_TIME_FORMATTER;
    }

    /**
     * 日期格式化
     */
    private static final DateTimeFormatter DATE_FORMATTER;

    public static DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }

    static{
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    }

    /**
     * 获取当前日期
     *
     * @return 东八区的当前日期
     */
    @NotNull
    public static LocalDate today() {
        return LocalDate.now(ZONE_ID);
    }


    /**
     * 获取当前日期时间
     * @return 东八区的日期时间
     */
    public static LocalDateTime now(){
        return LocalDateTime.now(ZONE_ID);
    }


    /**
     * 转换时间为字符串
     */
    @NotNull
    public static String format(@NotNull LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    /**
     * 调整当前时间
     * @param time 当前时间
     * @param number  偏移量
     * @param field 单位
     * @return  偏移后的单位
     */
    public static LocalDateTime offset(@NotNull LocalDateTime time, long number , ChronoUnit field) {
        return time.plus(number, field);
    }

    /**
     * 转换字符串为时间
     * @param time  字符串时间
     * @return 时间
     */
    @NotNull
    public static LocalDateTime parse(@NotNull String time){
        return LocalDateTime.parse(time, DATE_TIME_FORMATTER);
    }

}
