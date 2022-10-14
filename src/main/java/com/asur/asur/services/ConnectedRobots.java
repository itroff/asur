package com.asur.asur.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.asur.asur.services.mqttListeners.BooleanListener;
import com.asur.asur.services.mqttListeners.FloatDataListener;
import com.asur.asur.services.mqttListeners.MotorResListener;


@Component
public class ConnectedRobots {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectedRobots.class);
    //TODO multithread
    private Map<String, Robot> allRobots = new HashMap<>();

    @Autowired
    WebSocketSender webSocketSender;

    public void updateKeepAlive(String topicName) {
        try{
            String prefix = topicName.split("/")[1];
            if (allRobots.containsKey(prefix)) {
                Robot rb = allRobots.get(prefix);
                rb.setLastKeepAlive(LocalDateTime.now());
                rb.setStatus(RobotStatusEnum.ONLINE);
                webSocketSender.sendConnected(getAllRobots());
                
            } else {
                Robot rb = new Robot(0, topicName, prefix, 
                RobotStatusEnum.ONLINE, LocalDateTime.now());
                subscribeRobot(prefix);
                allRobots.put(prefix, rb);
                webSocketSender.sendConnected(getAllRobots());
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
        try {
            //Mqtt.getInstance().subscribe(MqttService.prefix + id + "/info/error", 1, new ErrorListener());
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/info/mode/res", 1, new BooleanListener());
            //Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/pos/res", 1, new CmdResListener());
            //Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/cargo/res", 1, new CmdResListener());
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/start/res", 1, new BooleanListener());
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/stop/res", 1, new BooleanListener());
        } catch (MqttException ex) {
            LOGGER.error(ex.getMessage());
        }
        List<String> topicsSensor = Arrays.asList( "Nav1", "Nav2", "LHD_R_L", "LHD_R_R", "LHD_F_L", "LHD_F_R", "Cargo_L", 
        "Cargo_R", "FNG_R_L_D", "FNG_R_L_U","FNG_R_R_D","FNG_R_L_U2","FNG_F_L_D","FNG_F_L_U","FNG_F_R_D","FNG_F_L_U2");
        String prefix = "/info/sensor/";
        for (String topic : topicsSensor) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + prefix + topic, 0, new BooleanListener());
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        // topics for  motorres
        List<String> topicsMotorsRes = Arrays.asList( "/motor/move/res", "/motor/lhd/res","/motor/fng_r_l/res",
        "/motor/fng_r_r/res", "/motor/fng_f_l/res", "/motor/fng_f_r/res"); 
        for (String topic : topicsMotorsRes) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + topic, 1, new MotorResListener());
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        // topics for floatdata
        List<String> topicsFloatData = Arrays.asList( "/param/move_vel/res","/param/lhd_time/res", "/param/fng_vel/res",
        "/param/lhd_ext/res"); 
        for (String topic : topicsMotorsRes) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + topic, 1, new FloatDataListener());
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    public List<Robot> getAllRobots() {
        return new ArrayList<Robot>(this.allRobots.values());
    }
    
}
