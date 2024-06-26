package com.fly.constant;

import java.time.ZoneId;

/**
 * 时间常量
 * @author Milk
 */
public abstract class TimeConst {

    /**
     * 北京时间(东八区)
     */
    public static final String BEIJING_TIME = "GMT+08:00";
    /**
     * 北京时间 ID
     */
    public static final ZoneId ZONE_ID = ZoneId.of(BEIJING_TIME);

    /**
     * 日期时间格式
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 一天开始时的 CRON 表达式
     */
    public static final String BEGIN_OF_DAY_CRON = "0 0 0 * * ?";

    /**
     * 每小时
     */
    public static final String BEGIN_OF_HOUR_CRON = "0 0 * * * ?";

    /**
     * 每两小时
     */
    public static final String EVERY_TWO_HOURS_CRON = "0 0 */2 * * *";
}
