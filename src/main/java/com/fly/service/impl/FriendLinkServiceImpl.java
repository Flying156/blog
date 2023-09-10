package com.fly.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.friendLink.FriendLinkDTO;
import com.fly.dto.friendLink.FriendLinkMangeDTO;
import com.fly.entity.FriendLink;
import com.fly.mapper.FriendLinkMapper;
import com.fly.service.FriendLinkService;
import com.fly.util.BeanCopyUtils;
import com.fly.util.ConvertUtils;
import com.fly.util.PageUtils;
import com.fly.util.StrRegexUtils;
import com.fly.vo.friendLink.FriendLinkVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fly.constant.CacheConst.FRIEND_LINK;


/**
 * @author Milk
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    /**
     * 分页查询友链数据
     * @param keywords 友链名称
     */
    @Override
    public PageDTO<FriendLinkMangeDTO> listAdminFriendLinks(String keywords) {
        IPage<FriendLink> page = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords), FriendLink::getLinkName, keywords)
                .page(PageUtils.getPage());
        return PageUtils.build(ConvertUtils.convertList(page.getRecords(), FriendLinkMangeDTO.class), page.getTotal());
    }

    /**
     * 新增或修改友链
     * @param friendLinkVO 更新的数据
     */
    @Override
    @CacheEvict(cacheNames = FRIEND_LINK, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO) {
        saveOrUpdate(BeanCopyUtils.copy(friendLinkVO, FriendLink.class));
    }

    /**
     * 批量试除友链
     * @param friendLinkIdList 友链列表
     */
    @Override
    @CacheEvict(cacheNames = FRIEND_LINK, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeFriendLink(List<Integer> friendLinkIdList) {
        removeBatchByIds(friendLinkIdList);
    }

    @Override
    @Cacheable(cacheNames = FRIEND_LINK,  key = "#root.methodName", sync = true)
    public List<FriendLinkDTO> listFriendLinks() {
        List<FriendLink> friendLinkList = baseMapper.selectList(null);
        return ConvertUtils.convertList(friendLinkList, FriendLinkDTO.class);
    }
}
