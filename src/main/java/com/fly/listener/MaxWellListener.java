package com.fly.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.fly.constant.RabbitMQConst;
import com.fly.dto.article.ArticleSearchDTO;
import com.fly.dto.article.MaxwellDataDTO;
import com.fly.entity.Article;
import com.fly.mapper.ArticleRepository;
import com.fly.util.BeanCopyUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.fly.constant.MaxWellConst.*;

/**
 * 使用 Maxwell 实时读取 MySQL 操作，当 RabbitMQ 监听数据库发生变化时，将文章数据同步到 Elasticsearch
 *
 * @author Milk
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitMQConst.MAXWELL_QUEUE)
public class MaxWellListener {

    @Resource
    private ArticleRepository articleRepository;

    /**
     * 通过 Maxwell 自动发送消息给消费者，以同步数据到 ES 中
     */
    @RabbitHandler
    public void process(byte [] jsonBytes, Channel channel, Message message) throws IOException{
        // 获取数据库的操作
        MaxwellDataDTO maxwellDataDTO = JSON.parseObject(jsonBytes, MaxwellDataDTO.class, JSONReader.Feature.FieldBased);
        Article article = JSON.parseObject(JSON.toJSONString(maxwellDataDTO.getData()), Article.class);


        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try{
            String type = maxwellDataDTO.getType();
            // 插入或更新则进行全量更新
            if(type.equals(INSERT) || type.equals(UPDATE)){
                articleRepository.save(BeanCopyUtils.copy(article, ArticleSearchDTO.class));
            }else if(type.equals(DELETE)){
                articleRepository.deleteById(article.getId());
            }
        } catch(Exception cause){
            // false: 进入私信队列
            channel.basicReject(deliveryTag, false);
            log.error("ES 文章操作异常", cause);
        }
        // false: 指的是消息队列只确认这一个消息
        channel.basicAck(deliveryTag, false);
    }
}
