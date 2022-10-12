package com.asur.asur.controllers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotStatusEnum;



@Controller
public class WebSocketController {

    private static final Logger LOGGER =  LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/robots")
    @SendTo("/topic/greetings")
    public Robot greeting(Robot robot) throws Exception {
        LOGGER.info(robot.toString());
        Thread.sleep(1000); // simulated delay
        return new Robot(0, "r2d2", "213", RobotStatusEnum.ONLINE, LocalDateTime.now());
    }
}
