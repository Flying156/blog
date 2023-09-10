package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.category.CategoryDTO;
import com.fly.dto.category.CategoryMangeDTO;
import com.fly.dto.category.CategoryOptionDTO;
import com.fly.entity.Category;
import com.fly.vo.category.CategoryVO;

import java.util.List;

/**
 * @author Milk
 */
public interface CategoryService extends IService<Category> {
    /**
     * 分页查询分类列表
     * @param keywords 分类关键词
     */
    PageDTO<CategoryMangeDTO> listAdminCategories(String keywords);

    /**
     * 修改或新增分类
     * @param categoryVO 需要修改的数据
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

    /**
     * 批量或单个删除分类
     * @param categoryIdList 分类列表
     */
    void removeCategories(List<Integer> categoryIdList);

    /**
     * 后台文章列表获取文章分类
     * @return 文章分类列表
     */
    List<CategoryOptionDTO> getAdminArticleCategories();

    /**
     * 前台分页查询分类
     * @return 文章分类分页查询
     */
    PageDTO<CategoryDTO> listCategories();
}
