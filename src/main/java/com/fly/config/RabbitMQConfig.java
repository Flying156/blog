package com.fly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.fly.constant.RabbitMQConst.*;

/**
 * RabbitMQConfig 配置类
 *
 * @author Milk
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送消息共有三种情况：
     * 1.没有找到 Exchange (交换机)
     * 2.没有找到 Queue (消息队列)
     * 3.发送成功
     * 采用回调函数防止消息丢失时系统不知
     * 执行回调函数： 一个是消息发给 Exchange 的回调函数
     * 另一个是发给 Queue 的回调函数
     */
    @PostConstruct
    public void configureRabbitTemplate(){
        // Exchange 回调函数
        RabbitTemplate.ConfirmCallback confirmCallback
                = (CorrelationData correlationData, boolean ack, String cause) ->{
            if(log.isDebugEnabled()){
                log.debug("{} ack = {} cause = {}", correlationData, ack, cause);
            }
            if(!ack){
                String message = String.format("消息发送给 Exchange 失败: %s Cause [%s]",
                        correlationData, cause);
                log.error(message);
            }
        };
        rabbitTemplate.setConfirmCallback(confirmCallback);

        // 发给队列的回调函数

        RabbitTemplate.ReturnsCallback returnsCallback = (ReturnedMessage returnedMessage) ->{
            String message = String.format("消息发给 Queue 失败: %s", returnedMessage);
            log.error(message);
        };
        rabbitTemplate.setReturnsCallback(returnsCallback);


    }


    /**
     * maxWell 交换机
     * 使用 elasticsearch 搜索时同步数据，实现异步调用
     */
    @Bean
    public FanoutExchange maxWellExchange(){
        return ExchangeBuilder.fanoutExchange(MAXWELL_EXCHANGE).build();
    }



    @Bean
    public Queue maxWellQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(X_DEAD_LETTER_EXCHANGE, DEAD_LETTER_QUEUE);
        arguments.put(X_DEAD_LETTER_ROUTING_KEY, ROUTING_KEY);
        return QueueBuilder.durable(MAXWELL_QUEUE).withArguments(arguments).build();
    }

    @Bean
    public Binding maxWellBinding(){
        return BindingBuilder.bind(maxWellQueue()).to(maxWellExchange());
    }

    /**
     * email队列，同时绑定死信交换机
     */
    @Bean
    public Queue emailQueue(){
        Map<String, Object> arguments = new HashMap<>();
        // 绑定死信交换机与私信队列
        arguments.put(X_DEAD_LETTER_EXCHANGE, DEAD_LETTER_QUEUE);
        // 设置死信交换机的路由键
        arguments.put(X_DEAD_LETTER_ROUTING_KEY, ROUTING_KEY);

        return QueueBuilder.durable(EMAIL_QUEUE).withArguments(arguments).build();
    }

    /**
     * email 广播交换机
     */
    @Bean
    public FanoutExchange emailExchange(){
        return ExchangeBuilder.fanoutExchange(EMAIL_EXCHANGE).build();
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }


    /**
     * 死信交换机
     */
    @Bean
    public FanoutExchange deadLetterExchange(){
        return ExchangeBuilder.fanoutExchange(DEAD_LETTER_EXCHANGE).build();
    }

    /**
     * 绑定私信队列和交换机
     */
    public Binding deadLetterBinding(){
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

}
