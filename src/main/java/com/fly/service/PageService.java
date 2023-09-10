package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.entity.Page;
import com.fly.vo.page.PageVO;

import java.util.List;

/**
 * @author Milk
 */
public interface PageService extends IService<Page> {
    /**
     * 后台查看页面数据
     * @return 页面数据
     */
    List<PageVO> listPages();

    /**
     * 保存或更新页面
     * @param pageVO 页面数据
     */
    void saveOrUpdatePage(PageVO pageVO);

    /**
     * 删除页面
     * @param pageId 页面 ID
     */
    void removePage(Integer pageId);

}
