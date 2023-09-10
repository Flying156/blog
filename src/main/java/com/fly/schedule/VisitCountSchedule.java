package com.fly.schedule;

import com.fly.dto.blogInfo.DailyVisitDTO;
import com.fly.util.RedisUtils;
import com.fly.util.TimeUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Optional;

import static com.fly.constant.GenericConst.*;
import static com.fly.constant.RedisConst.DAILY_VISIT_PREFIX;
import static com.fly.constant.TimeConst.BEGIN_OF_DAY_CRON;
import static com.fly.constant.TimeConst.BEIJING_TIME;

/**
 * 访问量统计定时任务
 *
 * @author Milk
 */
@Component
public class VisitCountSchedule {

    /**
     * 周访问数据的本地缓存
     */
    private static final LinkedList<DailyVisitDTO> WEEKLY_VISIT = new LinkedList<>();
    @Resource
    private RedisUtils redisUtils;

    /**
     * 初始化列表
     */
    @PostConstruct
    @SuppressWarnings("all")
    private void initialize(){
        // 将时间调回七天以前
        LocalDate date = TimeUtils.today().minusDays(SEVEN);
        for(int i = ZERO; i < SEVEN; i++){
            String dayKey = DAILY_VISIT_PREFIX + date;
            Object viewsCount = Optional.ofNullable(redisUtils.get(dayKey)).orElse(ZERO);
            DailyVisitDTO  dailyVisitDTO = DailyVisitDTO.builder()
                    .day(dayKey)
                    .viewsCount((Integer)viewsCount)
                    .build();
            WEEKLY_VISIT.addLast(dailyVisitDTO);
            date = date.plusDays(ONE);
        }
    }
    public static LinkedList<DailyVisitDTO> getWeeklyVisit(){
        return WEEKLY_VISIT;
    }

    @Async
    @Scheduled(cron = BEGIN_OF_DAY_CRON, zone = BEIJING_TIME)
    public void updateWeeklyVisitCount(){
        LocalDate yesterday = TimeUtils.today().minusDays(ONE);
        String yesterdayKey = DAILY_VISIT_PREFIX + yesterday;
        Object viewCount = Optional.ofNullable(RedisUtils.get(yesterdayKey)).orElse(ZERO);
        DailyVisitDTO dailyVisitDTO = DailyVisitDTO.builder()
                .day(yesterdayKey)
                .viewsCount((Integer)viewCount)
                .build();
        WEEKLY_VISIT.removeFirst();
        WEEKLY_VISIT.addLast(dailyVisitDTO);

    }

}
