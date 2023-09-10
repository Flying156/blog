package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.category.CategoryDTO;
import com.fly.dto.category.CategoryMangeDTO;
import com.fly.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 查询相关的分类以及文章数量
     */
    List<CategoryDTO> listCategories();

    /**
     * 管理端分页查询分类
     */
    List<CategoryMangeDTO> listAdminCategories(long offset, long size, String keywords);


}
