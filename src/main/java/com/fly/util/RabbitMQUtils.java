package com.fly.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.fly.dto.comment.EmailDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.fly.constant.RabbitMQConst.EMAIL_EXCHANGE;
import static com.fly.constant.RabbitMQConst.ROUTING_KEY;

/**
 * RabbitMQ 工具类
 *
 * @author Milk
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitMQUtils {

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate){
        RabbitMQUtils.rabbitTemplate = rabbitTemplate;
    }

    public static void sendEmail(EmailDTO emailDTO){
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, ROUTING_KEY,
                new Message(JSON.toJSONBytes(emailDTO, JSONWriter.Feature.FieldBased), new MessageProperties()));
    }


}
