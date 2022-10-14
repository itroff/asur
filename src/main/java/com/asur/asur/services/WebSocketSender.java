package com.asur.asur.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.asur.asur.models.Robot;

@Component
public class WebSocketSender {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void sendConnected(List<Robot> robots) throws Exception {
        this.simpMessagingTemplate.convertAndSend("/topic/connected", robots);
    }
    
}
