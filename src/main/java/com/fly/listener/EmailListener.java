package com.fly.listener;

import com.alibaba.fastjson2.JSON;
import com.fly.constant.RabbitMQConst;
import com.fly.dto.comment.EmailDTO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 邮件监听类
 *
 * @author Milk
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitMQConst.EMAIL_QUEUE)
public class EmailListener {

    @Value("${spring.mail.username}")
    private String email;

    @Resource
    private JavaMailSender javaMailSender;


    @RabbitHandler
    public void process(byte[] jsonBytes, Channel channel, Message message) throws IOException{
        EmailDTO emailDTO = JSON.parseObject(jsonBytes, EmailDTO.class);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(email);
        mailMessage.setTo(emailDTO.getEmail());
        mailMessage.setSubject(emailDTO.getSubject());
        mailMessage.setText(emailDTO.getContent());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            javaMailSender.send(mailMessage);
        } catch (MailException cause) {
            channel.basicReject(deliveryTag, false);
            log.error("邮件发送异常", cause);
        }
        channel.basicAck(deliveryTag, false);

    }

}
