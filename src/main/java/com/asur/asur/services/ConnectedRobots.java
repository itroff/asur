package com.asur.asur.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.config.Mqtt;
import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotParam;
import com.asur.asur.models.RobotStatusEnum;
import com.asur.asur.services.mqttListeners.BooleanListener;
import com.asur.asur.services.mqttListeners.ErrorListener;
import com.asur.asur.services.mqttListeners.FloatDataListener;
import com.asur.asur.services.mqttListeners.MotorResListener;


@Component
public class ConnectedRobots {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectedRobots.class);
    //TODO multithread
    private Map<String, Robot> allRobots = new ConcurrentHashMap<>();

    private Map<Robot, List<RobotParam>> params = new ConcurrentHashMap<>();

    @Autowired
    WebSocketSender webSocketSender;

    @Autowired
    private Diagnostics diagnostics;

    public void updateKeepAlive(String topicName) {
        try{
            String prefix = topicName.split("/")[1];
            if (allRobots.containsKey(prefix)) {
                Robot rb = allRobots.get(prefix);
                rb.setLastKeepAlive(LocalDateTime.now());
                if (rb.getStatus().equals(RobotStatusEnum.NO_LINK)) {
                    rb.setStatus(RobotStatusEnum.ONLINE);
                }
                webSocketSender.sendConnected(getAllRobots());
                
            } else {
                Robot rb = new Robot(0, topicName, prefix, 
                RobotStatusEnum.ONLINE, LocalDateTime.now());
                subscribeRobot(prefix);
                allRobots.put(prefix, rb);
                params.put(rb, new ArrayList<RobotParam>());
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
        boolean changed = false;
        for (Map.Entry<String, Robot> entry : allRobots.entrySet()) {
            if (entry.getValue().getLastKeepAlive().plusSeconds(2).isBefore(LocalDateTime.now())) {
                entry.getValue().setStatus(RobotStatusEnum.NO_LINK);
                changed = true;
            }
            if (entry.getValue().getStatus() == RobotStatusEnum.ONLINE) {
                robotsOnline++;
            }
        }
        if (changed) {
            webSocketSender.sendConnected(getAllRobots());
        }
      //  LOGGER.info("allRobots: {}, onlineRobots: {}", robotsNum, robotsOnline);
    }


    public void subscribeRobot(String id) {
        try {
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/info/error", 1, new ErrorListener(this));
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/info/mode/res", 1, new BooleanListener(this));
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/pos/res", 1, new MotorResListener(this)/*new CmdResListener()*/);
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/cargo/res", 1, new MotorResListener(this)/*new CmdResListener()*/);
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/start/res", 1, new BooleanListener(this));
            Mqtt.getInstance().subscribe(MqttService.prefix + id + "/action/stop/res", 1, new BooleanListener(this));
        } catch (MqttException ex) {
            LOGGER.error(ex.getMessage());
        }
        List<String> topicsSensor = Arrays.asList( "Nav1", "Nav2", "LHD_R_L", "LHD_R_R", "LHD_F_L", "LHD_F_R", "Cargo_L", 
        "Cargo_R", "FNG_R_L_D", "FNG_R_L_U","FNG_R_R_D","FNG_R_R_U","FNG_F_L_D","FNG_F_L_U","FNG_F_R_D","FNG_F_R_U");
        String prefix = "/info/sensor/";
        for (String topic : topicsSensor) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + prefix + topic, 0, new BooleanListener(this));
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        // topics for  motorres
        List<String> topicsMotorsRes = Arrays.asList( "/motor/move/res", "/motor/lhd/res","/motor/fng_r_l/res",
        "/motor/fng_r_r/res", "/motor/fng_f_l/res", "/motor/fng_f_r/res"); 
        for (String topic : topicsMotorsRes) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + topic, 1, new MotorResListener(this));
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        // topics for floatdata
        List<String> topicsFloatData = Arrays.asList( "/param/move_vel/res","/param/lhd_time/res", "/param/fng_vel/res",
        "/param/lhd_ext/res"); 
        for (String topic : topicsFloatData) {
            try {
                Mqtt.getInstance().subscribe(MqttService.prefix + id + topic, 1, new FloatDataListener(this));
            } catch (MqttException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    public List<Robot> getAllRobots() {
        return new ArrayList<Robot>(this.allRobots.values());
    }

    public synchronized void updateParam(String robotName, RobotParam param, DiagnosticElem elem) {

        Robot rb = allRobots.get(robotName);
        boolean finded = false;
        diagnostics.addElem(elem);
        List<RobotParam> lst = params.get(rb);
        for(RobotParam pr : lst){
            if (param.getParamName().equals(pr.getParamName())) {
                //pr = param;
                pr.updateParam(param);
         //       LOGGER.info("update param: {}", pr);
                return;
            }
        }
    //    LOGGER.info("add new param: {}", param);
        params.get(rb).add(param);
    }

    public List<RobotParam> getParamsForRobot(String robotName) {
        Robot rb = allRobots.get(robotName);
        return params.get(rb);
    }

    public Robot getRobot(String topicId) {
        return allRobots.get(topicId);
    }

    public RobotParam getParamRobot(String robotName, String paramName) {
        Robot rb = allRobots.get(robotName);
        List<RobotParam> paramsRobots =  params.get(rb);
        for(RobotParam param : paramsRobots) { 
            if (param.getParamName().equals(paramName)) {
                return param;
            }
        }
        return null;

    }

    public void updateRobotError(String topic, int errorCode) {
        Robot rb = allRobots.get(topic);
        if (errorCode == 0) {
            rb.setStatus(RobotStatusEnum.ONLINE);
        } else {
            rb.setStatus(RobotStatusEnum.FAULT);
        }
        webSocketSender.sendConnected(getAllRobots());
    }
    
}
