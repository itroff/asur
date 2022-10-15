package com.asur.asur.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.models.MqttConfig;
import com.asur.asur.models.PosCmd;
import com.asur.asur.models.Robot;

@Component
public class WebSocketSender {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void sendConnected(List<Robot> robots) {
        this.simpMessagingTemplate.convertAndSend("/topic/connected", robots);
    }

    public void sendConfig(MqttConfig config) {
        this.simpMessagingTemplate.convertAndSend("/topic/mqtt-config-status", config);
    }    

    public void sendDiagnostic(DiagnosticElem elem) {
        this.simpMessagingTemplate.convertAndSend("/topic/diagnostics", List.of(elem));
    }
}
