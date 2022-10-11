package com.asur.asur.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.config.Mqtt;
import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotStatusEnum;
import com.asur.asur.services.mqqttListeners.BooleanListener;


@Component
public class ConnectedRobots {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectedRobots.class);
    //TODO multithread
    private Map<String, Robot> allRobots = new HashMap<>();

    public void updateKeepAlive(String topicName) {
        try{
            String prefix = topicName.split("/")[1];
            if (allRobots.containsKey(prefix)) {
                Robot rb = allRobots.get(prefix);
                rb.setLastKeepAlive(LocalDateTime.now());
                rb.setStatus(RobotStatusEnum.ONLINE);
            } else {
                Robot rb = new Robot(0, topicName, prefix, 
                RobotStatusEnum.ONLINE, LocalDateTime.now());
                subscribeRobot(prefix);
                allRobots.put(prefix, rb);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.error("wrong parse from ID");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }
 
    @Scheduled(fixedRate = 1000)
    public void updateStatus() {
        int robotsNum = allRobots.size();
        int robotsOnline = 0;
        for (Map.Entry<String, Robot> entry : allRobots.entrySet()) {
            if (entry.getValue().getLastKeepAlive().plusSeconds(2).isBefore(LocalDateTime.now())) {
                entry.getValue().setStatus(RobotStatusEnum.NO_LINK);
            }
            if (entry.getValue().getStatus() == RobotStatusEnum.ONLINE) {
                robotsOnline++;
            }
        }
        LOGGER.info("allRobots: {}, onlineRobots: {}", robotsNum, robotsOnline);
    }


    public void subscribeRobot(String id) {
        List<String> topics = Arrays.asList("info/mode/res", "info/sensor/Nav1", "info/sensor/Nav2", 
        "info/sensor/LHD_R_L", "info/sensor/LHD_R_R", "info/sensor/LHD_F_L", "info/sensor/LHD_F_R", "info/sensor/Cargo_L", 
        "info/sensor/Cargo_R"); //TODO addd
        for (String topic : topics) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + topic, 0, new BooleanListener());
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }
    
}
