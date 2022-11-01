package com.asur.asur.controllers;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.asur.asur.config.Mqtt;
import com.asur.asur.generated.model.PosCmd.PosCmdProto;
import com.asur.asur.models.*;
import com.asur.asur.services.AutoControlService;
import com.asur.asur.services.ConnectedRobots;
import com.asur.asur.services.Diagnostics;
import com.asur.asur.services.MqttService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class WebSocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    ConnectedRobots connectedRobots;

    @Autowired
    Diagnostics diagnostics;

    @Autowired
    MqttService mqttService;

    @Autowired
    AutoControlService autoControlService;

    @MessageMapping("/robots")
    @SendTo("/topic/connected")
    public List<Robot> connected() throws Exception {
        // LOGGER.info("request connected");
        return connectedRobots.getAllRobots();
    }

    @MessageMapping("/mqtt-config-get")
    @SendTo("/topic/mqtt-conf")
    public MqttConfig getMqttConfig() throws Exception {
        // LOGGER.info("request mqtt");
        return Mqtt.getConfig();
    }

    @MessageMapping("/mqtt-config")
    public void setMqttConfig(MqttConfig config) {
        Mqtt.reconnect(config);
    }

    @MessageMapping("/cmd-bool")
    public void cmdBool(BoolCmd cmd) {
        // LOGGER.info(cmd.toString());
        mqttService.sendBoolCmd(cmd);
    }

    @MessageMapping("/cmd-pos")
    public void cmdPos(PosCmd cmd) {
        // LOGGER.info(cmd.toString());
        mqttService.sendPosCmd(cmd);
    }

    @MessageMapping("/cmd-cargo")
    public void cmdCargo(CargoCmd cmd) {
        // LOGGER.info(cmd.toString());
        mqttService.sendCargoCmd(cmd);
    }

    @MessageMapping("/cmd-motor")
    public void cmdMotor(MotorCmd cmd) {
        LOGGER.info(cmd.toString());
        mqttService.sendMotorCmd(cmd);
    }

    @MessageMapping("/cmd-param")
    public void cmdParam(CmdParam cmd) {
        // LOGGER.info(cmd.toString());
        mqttService.sendParamCmd(cmd);
    }

    @MessageMapping("/diagnostics")
    @SendTo("/topic/diagnostics")
    public List<DiagnosticElem> diagnostics() {
        return diagnostics.getDiagnostics();
    }

    @MessageMapping("/params")
    @SendTo("/topic/params")
    public List<RobotParam> params(String robotName) {
        // LOGGER.info(robotName);
        return connectedRobots.getParamsForRobot(robotName);
    }

    @MessageMapping("/cmd-auto")
    public void cmdAuto(AutoMsg msg) {
        if (msg.getCmd().equals(CmdControl.PLAY)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                FileMeta file = mapper.readValue(Paths.get("/asur/asur/src/main/resources/scenarious", msg.getFile() + ".json").toFile(), FileMeta.class);
                autoControlService.injectNewMission(file);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        autoControlService.controlStep(msg.getCmd());
        // autoControlService.saveDefaultFile();
        LOGGER.info(msg.toString());
    }

    @MessageMapping("/scenarious-get")
    @SendTo("/topic/scenarious")
    public List<String> getScenarious() throws Exception {
        // LOGGER.info("request mqtt");
        return autoControlService.getScenarious();
    }

}
