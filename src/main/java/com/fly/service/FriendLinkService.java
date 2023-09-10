package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.friendLink.FriendLinkDTO;
import com.fly.dto.friendLink.FriendLinkMangeDTO;
import com.fly.entity.FriendLink;
import com.fly.vo.friendLink.FriendLinkVO;

import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {
    /**
     * 后台友链数据
     * @param keywords 友链名称
     */
    PageDTO<FriendLinkMangeDTO> listAdminFriendLinks(String keywords);

    /**
     * 新增或修改友链
     * @param friendLinkVO 更新的数据
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

    /**
     * 批量删除友链
     * @param friendLinkIdList 友链列表
     */
    void removeFriendLink(List<Integer> friendLinkIdList);

    /**
     * 前台查看友链
     * @return 友链列表
     */
    List<FriendLinkDTO> listFriendLinks();
}
