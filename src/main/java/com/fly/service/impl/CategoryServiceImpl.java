package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.category.CategoryDTO;
import com.fly.dto.category.CategoryMangeDTO;
import com.fly.dto.category.CategoryOptionDTO;
import com.fly.entity.Article;
import com.fly.entity.Category;
import com.fly.mapper.ArticleMapper;
import com.fly.mapper.CategoryMapper;
import com.fly.service.CategoryService;
import com.fly.util.BeanCopyUtils;
import com.fly.util.ConvertUtils;
import com.fly.util.PageUtils;
import com.fly.util.StrRegexUtils;
import com.fly.vo.category.CategoryVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.fly.constant.GenericConst.ZERO_L;

/**
 * @author Milk
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public PageDTO<CategoryMangeDTO> listAdminCategories(String keywords) {
        Long categoryCount = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords), Category::getCategoryName, keywords)
                .count();
        // 如果分类数为零
        if(categoryCount.equals(ZERO_L)){
            return new PageDTO<>();
        }
        List<CategoryMangeDTO> adminCategoryList = baseMapper.listAdminCategories(PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(adminCategoryList, categoryCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        // 如果ID为空，则为新增
        boolean save = categoryVO.getId() == null;
        if(save){
            // 新增时如果存在重复分类名
            boolean exists = lambdaQuery()
                    .eq(Category::getCategoryName, categoryVO.getCategoryName())
                    .exists();
            if(exists){
                throw new ServiceException("分类名已存在");
            }
        }
        Category category = BeanCopyUtils.copy(categoryVO, Category.class);
        saveOrUpdate(category);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeCategories(List<Integer> categoryIdList) {
        // 首先判断分类是否关联文章
        boolean exists = new LambdaQueryChainWrapper<>(articleMapper)
                .in(Article::getCategoryId, categoryIdList)
                .exists();
        if(exists){
            throw new ServiceException("删除失败，该分类下存在文章");
        }
        removeBatchByIds(categoryIdList);
    }

    @Override
    public List<CategoryOptionDTO> getAdminArticleCategories() {
        List<Category> categoryList = lambdaQuery()
                .select(Category::getId, Category::getCategoryName)
                .orderByDesc(Category::getId).list();
        return ConvertUtils.convertList(categoryList, CategoryOptionDTO.class);
    }

    @Override
    public PageDTO<CategoryDTO> listCategories() {
//        Long categoryArticleCount = baseMapper.listArticleByCategory();
        return PageUtils.build(baseMapper.listCategories(), count());
    }
}
