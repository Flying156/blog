package com.fly.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fly.constant.GenericConst.ONE_L;
import static com.fly.constant.GenericConst.TRUE_OF_LONG;

/**
 * Redis相关操作类
 *
 * @author Milk
 */
@Component
public class RedisUtils {
    /**
     * 多线程变量，封装Redis操作类
     */
    private static volatile RedisTemplate<String, Object> redisTemplate;


    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    // region 通用

    /**
     * 删除给定的一个或多个 key, 不存在的 key 将被忽略
     *
     * @param key  键
     * @return    删除的键数量
     */
    public static Long del(@NotNull String... key){
        return redisTemplate.delete(Arrays.asList(key));
    }

    /**
     * 删除多个key
     * @param keys 键
     * @return    删除键数量
     */
    public static Long del(Set<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 删除key
     * @param key
     */
    public static void del(@NotNull String key){
        del(getKeys(key));
    }

    /**
     * 获取模式的所有键
     * @param key 模式
     * @return 键
     */
    public static Set<String> getKeys(@NotNull String key){
        return redisTemplate.keys(key);
    }


    /**
     * 为给定 key 设置生存时间，当 key 过期时，自动删除
     * <p>
     *     可以给一个已经带有生存时间的 key 执行 expire 命令，新指定的生存时间
     *     会取代旧的生存时间
     * </p>
     *
     * @param key      键
     * @param timeout  时间量
     * @param unit     时间单位
     * @return     设置成功时返回 true, key 不存在或不能为 key 设置生存时间段，返回false
     */
    public static Boolean expire(@NotNull String key, long timeout, @NotNull TimeUnit unit){
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 在管道连接上执行给定的 Redis 会话。允许事务流水线化。
     * <p>
     * <b>请注意，回调 不能 返回非 null 值，因为它会被管道覆盖。</b>
     *
     * @param sessionCallback 会话回调
     * @return 管道返回的对象列表
     */
    @SuppressWarnings("rawtypes")
    public static List executePipelined(SessionCallback sessionCallback) {
        return redisTemplate.executePipelined(sessionCallback);
    }

    // endregion

    // region 字符串

    /**
     * 将对应的值加一
     * <p>
     *     如果对应的键 key 不存在，它的值将会初始化为 0， 然后执行 incr 命令
     * </p>
     *
     * @param key 键
     * @return  影响的值
     */
    public static Long incr(@NotNull String key){return redisTemplate.opsForValue().increment(key);}

    /**
     * 删除对应的键值对
     *
     * @param key   键
     * @param value 值
     */
    public static void set(@NotNull String key, @NotNull Object value){
       redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 通过key获取相应的值
     *
     * @param key 最简单的数据结构
     * @return  对应的值
     */
    public static Object get(@NotNull String key){
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * 将键 key 的值设置为 value, 同时设置键 key 的生存时间
     * <p>
     *     键 key 已经存在， setEx 命令将覆盖已有的值
     * </p>
     *
     * @param key     键
     * @param value   值
     * @param timeout 时间段
     * @param unit    时间单位
     */
    public static void setEx(@NotNull String key, @NotNull String value, long timeout, @NotNull TimeUnit unit){
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 将键 key 的值设置为 value，同时设置键 key 的生存时间。
     * <p>
     * 如果键 key 已经存在，那么 setNX 命令没有效果。
     * <p>
     * setNX 是一个原子（atomic）操作，
     *
     * @param key     键
     * @param value   值
     * @param timeout 时间量
     * @param unit    时间单位
     */
    public static void setNx(@NotNull String key, @NotNull String value, long timeout, @NotNull TimeUnit unit){
        redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    // endregion


    // region 哈希表

    /**
     * 返回哈希表特定的值
     *
     * @param key   键
     * @param field 域
     * @return  如果键或域不存在，返回 null
     */
    @Nullable
    public static Object hGet(@NotNull String key, @NotNull String field){
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 为哈希表 key 中域 field 的值加上增量 increment
     * <p>
     *     增量可以为负数
     *     如果 key 不存在， 一个新的翰洗标被创建并执行 increment命令
     *     如果指定的字段不存在， 执行命令前， 字段的值将被初始化为 0
     * </p>
     *
     * @param key     键
     * @param field   域
     * @param increment  增量
     * @return  执行 increment 命令后， 哈希表 key 中域 field 的值
     */
    @NotNull
    public static Long hIncrBy(@NotNull String key, @NotNull String field, long increment){
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 获取 redis 中所有哈希表的数据
     *
     * @param key   键
     * @return     redis 中哈希表的数据
     */
    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map<String, Object> hGetAll(@NotNull String key){
        return (Map)redisTemplate.opsForHash().entries(key);

    }

    // endregion

    /**
     * 点赞文章或取消点赞
     * @param setKeyPrefix 集合键前缀，与 userInfoId 组成键，每个集合对应于一个用户的点赞数据。
     * @param hashKey      哈希表的键，一个哈希表对应一种内容的点赞数据。
     * @param contentId    点赞内容的 ID，作为集合的元素和哈希表的域名，每个域名对应一个内容的点赞数据。
     */
    public static void likeOrUnlike(@NotNull String setKeyPrefix, @NotNull String hashKey, @NotNull Integer contentId) {
        String setKey = setKeyPrefix + SecurityUtils.getUserInfoId();
        // 通过 set 判断是否插入成功
        Long like = RedisUtils.sAdd(setKey, contentId);
        if(like.equals(TRUE_OF_LONG)){
            RedisUtils.hIncrBy(hashKey, contentId.toString(), ONE_L);
        }else{
            RedisUtils.sRemove(setKey, contentId);
            RedisUtils.hIncrBy(hashKey, contentId.toString(), -ONE_L);
        }
    }

    // region 集合

    /**
     * 加一个或多个元素加入到 redis中的 set 中，由于set特性，重复的 member 将会被忽略
     *
     * @param key   键
     * @param member 成员
     * @return  将元素添加到redis中
     */
    public static Long sAdd(@NotNull String key, @NotNull Object... member){
        return redisTemplate.opsForSet().add(key, member);
    }

    /**
     * 判断 member 是否是集合 key 的成员
     *
     * @param key    键
     * @param member 可能的成员
     * @return 如果 member 元素是集合的成员，返回 true
     * 如果 member 元素不是集合的成员，或 key 不存在， 返回 false
     */
    public static Boolean sIsMember(@NotNull String key, Object member){
        return redisTemplate.opsForSet().isMember(key, member);
    }


    /**
     * 根据键返回集合
     *
     * @param key 键
     * @return 集合
     */
    public static Set<Object> sMembers(@NotNull String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 获取对应的集合大小
     *
     * @param key 键
     * @return  相对应集合大小
     */
    public static Long sCard(@NotNull String key){
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除一个或多个member
     *
     * @param key  键
     * @param member  集合成员
     * @return  影响成员的数量
     */
    public static Long sRemove(@NotNull String key, @NotNull Object... member){
        return redisTemplate.opsForSet().remove(key, member);
    }


    // endregion

    // region 有序集合


    /**
     * 返回有序集合 key 中，成员 member 的 score 值
     *
     * @param key    键
     * @param member 成员
     * @return  成员 member 的 score
     */
    public static Double zScore(@NotNull String key, Object member){
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
     * <p>
     * 可以通过传递一个负数值 increment ，让 score 减去相应的值。
     * <p>
     * 当 key 不存在，或 member 不是 key 的成员时，则进行新建。
     *
     * @param key       键
     * @param member    成员
     * @param increment 增量
     * @return member 成员的新 score 值。
     */
    public static Double zIncBy(@NotNull String key, Object member, double increment){
        return redisTemplate.opsForZSet().incrementScore(key, member, increment);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员和分数的映射。
     * <p>
     * 其中成员的位置按 score 值递增（从小到大）来排序。
     * <p>
     * 具有相同 score 值的成员按字典序来排列。
     * <p>
     * 下标参数 start 和 end 都以 0 为底，也就是说，以 0 表示有序集第一个成员，
     * 以 1 表示有序集第二个成员，以此类推。你也可以使用负数下标，以 -1 表示最后
     * 一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。比如说，当 start 的值比有序集的最大下标还要
     * 大，或是 start > stop 时，zRange 命令只是简单地返回一个空列表。 另一方
     * 面，假如 end 参数的值比有序集的最大下标还要大，那么 Redis 将 end 当作
     * 最大下标来处理。
     *
     * @param key   键，不能为空
     * @param start 起始下标
     * @param end   终止下标
     * @return 成员和分数的映射，按分数升序排序。
     */
    @NotNull
    @SuppressWarnings("all")
    public static Map<Object, Double> zRangeWithScores(@NotNull String key, int start, int end){
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore,
                        Double::sum, LinkedHashMap::new));
    }

    /**
     * 返回有序集 key 中，指定区间内的成员和分数的映射。
     * <p>
     * 除了成员按 score 值递减的次序排列这一点外，该命令的其他方面
     * 和 {@link RedisUtils#zRangeWithScores(String, int, int)} 命令一样。
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 成员和分数的映射，按分数降序排序。
     */
    @NotNull
    @SuppressWarnings("all")
    public static Map<Object, Double> zRevRangeWithScores(@NotNull String key, int start, int end){
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore,
                        Double::sum, LinkedHashMap::new));
    }


    // endregion

    /**
     * 将任意数量的元素添加到指定的 HyperLogLog 里面。
     *
     * @param key    键
     * @param values 任意数量的元素
     * @return 如果 HyperLogLog 的内部储存被修改了，那么返回 1，否则返回 0。
     */
    @NotNull
    public static Long pfAdd(@NotNull String key, @NotNull Object... values){
        return redisTemplate.opsForHyperLogLog().add(key, values);
    }

    /**
     * 当 pfCount 命令作用于单个键时，返回储存在给定键的 HyperLogLog 的近似基数，
     * 如果键不存在，那么返回 0。
     * <p>
     * 当 pfCount 命令作用于多个键时，返回所有给定 HyperLogLog 的并集的近似基数，
     * 这个近似基数是通过将所有给定 HyperLogLog 合并至一个临时 HyperLogLog 来计算得出的。
     *
     * @param keys 单个或多个键
     * @return 给定 HyperLogLog 包含的唯一元素的近似数量。
     */
    @NotNull
    public static Long pfCount(@NotNull String... keys) {
        return redisTemplate.opsForHyperLogLog().size(keys);
    }
}
