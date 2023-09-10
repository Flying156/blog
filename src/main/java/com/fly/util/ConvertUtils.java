package com.fly.util;

import com.sun.istack.internal.NotNull;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 转换工具类
 */
public abstract class ConvertUtils {

    /**
     * 转换成目标类
     * @param source 源对象
     * @param tClass 目标的Class
     * @return      目标类的对象
     */
    @NotNull
    public static <T> T convert(@Nullable Object source, @NotNull Class<T> tClass){
        T target;
        try{
            target = tClass.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("对象转换异常", e);
        }
        if(source != null){
            BeanUtils.copyProperties(source, target);
        }
        return target;
    }

    /**
     * 用给定的集合生成目标类的列表
     * @param collection 源集合
     * @param tClass  目标的Class
     * @return 目标类的列表
     */
    @NotNull
    public static <T> List<T> convertList(@Nullable Collection<?>collection, @NotNull Class<T> tClass){
        return getCollection(collection, ArrayList::new, tClass);
    }

    /**
     * 通过给定的源集合类生成目标类的目标集合
     * @param collection 源集合
     * @param cSupplier 目标集合
     * @param tClass 目标类的Class
     * @return  目标类的目标集合
     */
    public static <T, C extends Collection<T>> C getCollection
            (@Nullable Collection<?> collection, @NotNull Supplier<C> cSupplier, @NotNull Class<T> tClass){
        if(collection == null){
            return cSupplier.get();
        }
        return collection
                .stream()
                .map(element -> BeanCopyUtils.copy(element, tClass))
                .collect(Collectors.toCollection(cSupplier));
    }

    /**
     * 将类对象的引用 Set 集合转换为目标类或接口的引用 Set 集合
     * <p>
     * 用于判空和限制编译器警告。
     *
     * @param set 类对象的引用集合
     * @return 目标类或接口的引用 Set 集合
     */
    public static <T> Set<T> castSet(@Nullable Object set) {
        if(set == null){
            return new HashSet<>();
        }
        return cast(set);
    }

    /**
     * 将集合的引用转换为目标集合的引用
     * 用户限制编译器警告。
     *
     * @param collection 集合引用
     * @return 目标集合引用
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <C extends Collection<T>, T> C cast(@NotNull Object collection){
        return ((C) collection);
    }
}
