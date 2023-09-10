package com.fly.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fly.dto.chatRecord.ChatMessageDTO;
import com.fly.dto.chatRecord.ChatRecordDTO;
import com.fly.dto.chatRecord.RecalledMessageDTO;
import com.fly.entity.ChatRecord;
import com.fly.enums.ChatEnum;
import com.fly.service.ChatRecordService;
import com.fly.util.StrRegexUtils;
import com.fly.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.fly.constant.WebSocketConst.PONG;
import static com.fly.constant.WebSocketConst.X_FORWARDED_FOR;


/**
 * WebSocket 处理
 *
 * @author Milk
 */
@Slf4j
@Component
@SuppressWarnings("unused")
@ServerEndpoint(value = "/websocket", configurator = WebSocketHandler.ChatConfigurator.class)
public class WebSocketHandler {

    /**
     * 创建一个线程安全的 Set 集合, 存储每个客户端对应的对象
     */
    private static final Set<WebSocketHandler> WEB_SOCKET_HANDLER_SET = Collections.newSetFromMap(new ConcurrentHashMap<>());



    private static ChatRecordService chatRecordService;

    @Autowired
    public void setChatRecordService(ChatRecordService chatRecordService){
        WebSocketHandler.chatRecordService = chatRecordService;
    }

    /**
     * 记录会话
     */
    private Session session;


    /**
     * 获取客户端 IP，以便获取历史消息
     */
    public static class ChatConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            String ipAddress = WebUtils.getCurrentIpAddress();
            if(log.isDebugEnabled()){
                log.debug("WebSocket 握手，HandshakeRequest 请求头: {}", request.getHeaders());
                log.debug("WebSocket 握手，客户端 IP: {}", ipAddress);
            }

            sec.getUserProperties().put(X_FORWARDED_FOR, ipAddress);
        }
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        System.out.println("测试");
        System.out.println("连接成功");
        WEB_SOCKET_HANDLER_SET.add(this);
        // 更新在线人数
        chatRecordService.updateOnlineUserCount(WEB_SOCKET_HANDLER_SET.size());

        String ipAddress = endpointConfig.getUserProperties().get(X_FORWARDED_FOR).toString();
        // 获取历史消息
        ChatRecordDTO chatRecord = chatRecordService.getHistoryMessage(ipAddress);
        // 发送消息
        ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                .type(ChatEnum.HISTORY_MESSAGE.getType())
                .data(chatRecord)
                .build();
        sendMessage(session, messageDTO, "发送历史聊天记录异常");
    }


    /**
     * 关闭连接的方法，同时更新在线人数
     */
    @OnClose
    public void onClose() {
        System.out.println("关闭");
        // 移除会话
        WEB_SOCKET_HANDLER_SET.remove(this);
        // 更新在线人数
        chatRecordService.updateOnlineUserCount(WEB_SOCKET_HANDLER_SET.size());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        ChatMessageDTO messageDTO = JSON.parseObject(message, ChatMessageDTO.class, JSONReader.Feature.FieldBased);

        ChatEnum chatEnum = ChatEnum.get(messageDTO.getType());
        switch (chatEnum) {
            case SEND_MESSAGE:
                ChatRecord chatRecord = JSON.parseObject(
                        JSON.toJSONString(messageDTO.getData(), JSONWriter.Feature.FieldBased), ChatRecord.class, JSONReader.Feature.FieldBased);
                chatRecord.setContent(StrRegexUtils.filter(chatRecord.getContent()));
                chatRecordService.save(chatRecord);
                broadcastMessage(messageDTO);
                break;
            case RECALL_MESSAGE:
                // 撤回的消息
                RecalledMessageDTO recalledMessageDTO = JSON.parseObject
                        (JSON.toJSONString(messageDTO.getData(), JSONWriter.Feature.FieldBased), RecalledMessageDTO.class, JSONReader.Feature.FieldBased);
                // 删除消息记录
                chatRecordService.removeById(recalledMessageDTO.getId());
                broadcastMessage(messageDTO);
                break;
            case HEART_BEAT:
                // 心跳消息
                messageDTO.setData(PONG);
                sendMessage(session, messageDTO, "心跳消息异常");
                break;
        }
    }

    /**
     * 连接错误的方法
     */
    @OnError
    public void onError(Session session, Throwable cause) {
        onClose();
        log.error("WebSocket 连接错误:", cause);
    }


    private static void sendMessage(Session session, ChatMessageDTO chatMessageDTO, String error){
        try{
            session.getBasicRemote().sendText(JSON.toJSONString(chatMessageDTO, JSONWriter.Feature.FieldBased));
        } catch (IOException cause){
            throw new RuntimeException(error, cause);
        }
    }

    public static void broadcastMessage(ChatMessageDTO chatMessageDTO){
        for(WebSocketHandler receiver : WEB_SOCKET_HANDLER_SET){
            sendMessage(receiver.session, chatMessageDTO, "广播消息异常");
        }
    }


}
