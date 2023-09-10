package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.entity.Page;
import com.fly.mapper.PageMapper;
import com.fly.service.PageService;
import com.fly.util.BeanCopyUtils;
import com.fly.util.ConvertUtils;
import com.fly.vo.page.PageVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fly.constant.CacheConst.PAGE;


/**
 * @author Milk
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {

    /**
     * 后台页面列表
     */
    @Override
    @Cacheable(cacheNames = PAGE, key = "#root.methodName", sync = true)
    public List<PageVO> listPages() {
        return ConvertUtils.convertList(list(), PageVO.class);
    }

    @Override
    @CacheEvict(cacheNames = PAGE, allEntries = true)
    public void removePage(Integer pageId) {
        removeById(pageId);
    }

    @Override
    @CacheEvict(cacheNames = PAGE, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdatePage(PageVO pageVO) {
        saveOrUpdate(BeanCopyUtils.copy(pageVO, Page.class));
    }
}
