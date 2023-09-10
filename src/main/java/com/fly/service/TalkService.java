package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.talk.TalkDTO;
import com.fly.dto.talk.TalkMangeDTO;
import com.fly.entity.Talk;
import com.fly.vo.talk.TalkVO;

import java.util.List;

/**
 * @author Milk
 */
public interface TalkService extends IService<Talk> {
    /**
     * 后台获得说说列表
     */
    PageDTO<TalkMangeDTO> listAdminTalks(Integer status);

    /**
     * 更新或修改说说
     * @param talkVO 说说数据
     */
    void saveOrUpdateTalk(TalkVO talkVO);

    /**
     * 删除说说
     * @param talkIdList 列表Id
     */
    void removeTalks(List<Integer> talkIdList);

    /**
     * 根据 ID 获取说说内容
     * @param talkId ID
     * @return 说说数据
     */
    TalkMangeDTO getAdminTalk(Integer talkId);

    /**
     * 前台分页查看说说
     * @return 说说分页列表
     */
    PageDTO<TalkDTO> listTalks();

    /**
     * 首页查询说说列表
     * @return 说说列表
     */
    List<String> listHomePageTalks();
}
