package com.fly.strategy.impl;

import com.fly.enums.ArticleStatusEnum;
import com.fly.enums.ArticleTypeEnum;
import com.fly.strategy.ImportStrategy;
import com.fly.util.FileIoUtils;
import com.fly.util.StrRegexUtils;
import com.fly.util.TimeUtils;
import com.fly.vo.article.ArticleVO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fly.constant.GenericConst.EMPTY_STR;
import static com.fly.constant.GenericConst.ZERO;
import static com.fly.constant.HexoConst.*;
import static com.fly.constant.ImportConst.NORMAL;

/**
 * @author Milk
 */
@Component
public class ImportStrategyImpl implements ImportStrategy {


    @Override
    public ArticleVO importArticle(MultipartFile multipartFile, String strategyName) {
        strategyName = Optional.ofNullable(strategyName).orElse(NORMAL);

        ArticleVO articleVO;
        try{
            articleVO = strategyName.equals(NORMAL) ? getNormalArticleVO(multipartFile) : getHexoArticleVO(multipartFile);
        }catch (Exception cause){
            throw new RuntimeException("文章导入失败", cause);
        }
        return articleVO;
    }

    /**
     * 普通文章读入
     */
    public ArticleVO getNormalArticleVO(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        if(StrRegexUtils.isBlank(fileName)){
            throw new RuntimeException("文件名获取错误");
        }
        String articleTitle = FileIoUtils.getMainName(fileName);
        String articleContent;
        try(InputStream inputStream = multipartFile.getInputStream()) {
            byte[] bytes = FileIoUtils.readBytes(inputStream);
            articleContent = new String(bytes, StandardCharsets.UTF_8);
            // 默认为草稿
        }
        return ArticleVO.builder()
                .articleContent(articleContent)
                .articleTitle(articleTitle)
                .status(ArticleStatusEnum.DRAFT.getStatus())
                .build();
    }

    /**
     * Hexo 文章导入
     */
    public ArticleVO getHexoArticleVO(MultipartFile multipartFile) throws IOException {
        try(InputStream inputStream = multipartFile.getInputStream()){
            return parseLine(FileIoUtils.readLines(inputStream));
        }catch (Exception cause){
            throw new RuntimeException("无法读取", cause);
        }
    }

    /**
     * 对读取的每行进行解析
     * @return 文章数据
     */
    private ArticleVO parseLine(List<String> articleLine) {
        LocalDateTime createTime = TimeUtils.now();
        String articleTitle = EMPTY_STR, categoryName = EMPTY_STR;
        List<String> tagNameList = new ArrayList<>();
        StringBuilder articleContent = new StringBuilder();
        // 分隔符数量
        Integer delimiterCount = ZERO;
        // 标记出现的时标签还是分类
        Integer nameTypeFlag = NORMAL_FLAG;
        // 按行拼接
        for(String line : articleLine){
            if(delimiterCount.equals(MAX_DELIMITER_COUNT)){
                articleContent.append(line).append(LINE_FEED);
                continue;
            }else if(line.equals(DELIMITER)){
                delimiterCount++;
                continue;
            }

            // 获取文章标题
            if(line.startsWith(TITLE_PREFIX)){
                articleTitle = line.replace(TITLE_PREFIX, EMPTY_STR).trim();
                continue;
            }

            // 获取分类
            if(line.startsWith(CATEGORY_PREFIX)){
                categoryName = line.replace(CATEGORY_PREFIX, EMPTY_STR).trim();
                continue;
            }
            // 获取标签
            if(line.startsWith(TAG_PREFIX)){
                nameTypeFlag = TAG_FLAG;
                continue;
            }
            // 创建时间
            if(line.startsWith(DATE_PREFIX)){
                createTime = TimeUtils.parse(line.replace(DATE_PREFIX, EMPTY_STR).trim());
                continue;
            }
            // 读取目录标签内容
            if(line.startsWith(CATEGORY_OR_TAG_NAME_PREFIX)){
                if(nameTypeFlag.equals(CATEGORY_FLAG)){
                    categoryName = line.replace(CATEGORY_PREFIX, EMPTY_STR).trim();
                }else if(nameTypeFlag.equals(TAG_FLAG)){
                    tagNameList.add(line.replace(TAG_PREFIX, EMPTY_STR).trim());
                }
            }

        }
        // 如果标签或分类为空，设置为草稿
        Integer articleStatus = ArticleStatusEnum.PUBLIC.getStatus();
        if(StrRegexUtils.isBlank(categoryName) || CollectionUtils.isEmpty(tagNameList)){
            articleStatus = ArticleStatusEnum.DRAFT.getStatus();
        }

        // 默认为原创
        Integer articleType = ArticleTypeEnum.ORIGINAL.getType();
        return ArticleVO.builder()
                .articleTitle(articleTitle)
                .status(articleStatus)
                .tagNameList(tagNameList)
                .categoryName(categoryName)
                .type(articleType)
                .articleContent(articleContent.toString())
                .createTime(createTime)
                .build();
    }

}
