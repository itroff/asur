package com.asur.asur.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.asur.asur.config.Mqtt;
import com.asur.asur.models.CommandWebSocket;
import com.asur.asur.models.MqttConfig;
import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotStatusEnum;
import com.asur.asur.services.ConnectedRobots;
import com.asur.asur.services.MqttService;



@Controller
public class WebSocketController {

    private static final Logger LOGGER =  LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    ConnectedRobots connectedRobots;

    @Autowired
    MqttService mqttService;

    @MessageMapping("/robots")
    @SendTo("/topic/connected")
    public List<Robot> connected() throws Exception {
        LOGGER.info("request connected");
        return connectedRobots.getAllRobots();
    }

    @MessageMapping("/mqtt-config-get")
    @SendTo("/topic/mqtt-conf")
    public MqttConfig getMqttConfig() throws Exception {
        LOGGER.info("request mqtt");
        return Mqtt.getConfig();
    }

    @MessageMapping("/mqtt-config")
    public void setMqttConfig(MqttConfig config) {
        Mqtt.reconnect(config);
    }

    @MessageMapping("/cmd-bool")
    public void setMqttConfig(CommandWebSocket cmd) {
        LOGGER.info(cmd.toString());
        mqttService.sendBoolCmd(cmd.getRobot(), cmd.getCmd());
    }


}
