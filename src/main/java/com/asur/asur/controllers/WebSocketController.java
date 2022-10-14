package com.asur.asur.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotStatusEnum;
import com.asur.asur.services.ConnectedRobots;



@Controller
public class WebSocketController {

    private static final Logger LOGGER =  LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    ConnectedRobots connectedRobots;

    @MessageMapping("/robots")
    @SendTo("/topic/connected")
    public List<Robot> greeting() throws Exception {
        LOGGER.info("request connected");
       // Thread.sleep(1000); // simulated delay
       return connectedRobots.getAllRobots();
        //return new Robot(0, "r2d2", "213", RobotStatusEnum.ONLINE, LocalDateTime.now());
    }
}
