package com.fly.schedule;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fly.dto.userAuth.AreaCountDTO;
import com.fly.entity.UserAuth;
import com.fly.mapper.UserAuthMapper;
import com.fly.util.ConvertUtils;
import com.fly.util.RedisUtils;
import com.fly.util.StrRegexUtils;
import com.fly.util.WebUtils;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fly.constant.RedisConst.*;
import static com.fly.constant.TimeConst.*;

/**
 * 定时扫描访问用户地区
 *
 * @author Milk
 */
@Component
public class AreaCountSchedule {

    @Resource
    public  RedisUtils redisUtils;

    @Resource
    private UserAuthMapper userAuthMapper;

    @Resource
    private WebUtils webUtils;

    /**
     * 获取游客地域分布
     */
    public static List<AreaCountDTO> getVisitorAreaCount(){
        return ConvertUtils.cast(RedisUtils.get(VISITOR_AREA));
    }

    /**
     * 获取用户的地域分布
     */
    public static List<AreaCountDTO> getUserAreaCount(){
        return ConvertUtils.cast(RedisUtils.get(USER_AREA));
    }

    @PostConstruct
    public void update(){
        updateUserAreaCount();
        updateVisitorAreaCount();
    }

    /**
     * 定时扫描对应的地区人数
     */
    @Async
    @Scheduled(cron = BEGIN_OF_HOUR_CRON, zone = BEIJING_TIME)
    @SuppressWarnings("all")
    public void updateVisitorAreaCount(){
        List<AreaCountDTO> visitorAreaCountDTOList = redisUtils.sMembers(PROVINCE)
                .stream()
                .map(pronvince -> AreaCountDTO.builder()
                        .name((String)pronvince)
                        .value(redisUtils.pfCount(VISITOR_PROVINCE_PREFIX + pronvince))
                        .build())
                .collect(Collectors.toList());
        redisUtils.set(VISITOR_AREA, visitorAreaCountDTOList);
    }

    @Async
    @Scheduled(cron = EVERY_TWO_HOURS_CRON, zone = BEIJING_TIME)
    @SuppressWarnings("all")
    public void updateUserAreaCount(){
        Map<String, Long> userAreaCount = new LambdaQueryChainWrapper<UserAuth>(userAuthMapper)
                .select(UserAuth::getIpAddress)
                .list().stream()
                // 根据 IP 地址获取省份
                .map(userAuth -> webUtils.getInfo(userAuth.getIpAddress(), IpInfo::getProvince))
                .filter(StrRegexUtils::isNotBlank)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<AreaCountDTO> userAreaCountDTOList = userAreaCount.entrySet()
                .stream()
                .map(entry -> AreaCountDTO.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        redisUtils.set(USER_AREA, userAreaCountDTOList);
    }
}
