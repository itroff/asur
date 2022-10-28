package com.asur.asur.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.models.CmdControl;
import com.asur.asur.models.CmdElem;
import com.asur.asur.models.FileMeta;
import com.asur.asur.models.RobotParam;
import com.asur.asur.models.StateControl;

@Component
public class AutoControlService  {
    // STATEs: WAITING, WAITINGRES, RUN, STOP, PAUSE
    // cmds: PLAY, PAUSE, STOP, cmdResponse
    // 
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoControlService.class);

    @Autowired
    WebSocketSender webSocketSender;

    @Autowired
    ConnectedRobots connectedRobots;

    public AutoControlService() {
  
    }
    private FileMeta currentMission;

    private StateControl state = StateControl.STOP;
    private boolean isPaused = false;
    private CmdElem currentCmd = null;
    private LocalDateTime lastSend = null;


    public void controlStep(CmdControl cmd) {
        if (cmd.equals(CmdControl.STOP)) {
            state = StateControl.STOP;
            LOGGER.info("Stop Mission");
            webSocketSender.sendAutoControl("Stop Mission", StateControl.STOP);
        } else if (cmd.equals(CmdControl.PLAY)) {
            if (state.equals(StateControl.STOP)) {
                startMission();
            } else if (cmd.equals(CmdControl.PAUSE)){
                execCmd(currentMission.getCmds().get(0));
            } else {
                webSocketSender.sendAutoControl("Mission already started, please stop first", StateControl.RUN);
            }
        } else if (cmd.equals(CmdControl.PAUSE)) {
            if (state.equals(StateControl.WAITINGRES) || state.equals(StateControl.RUN)) {
                state = StateControl.PAUSE;
                webSocketSender.sendAutoControl("Mission paused", StateControl.PAUSE);
            }
        } else if (cmd.equals(CmdControl.RESPONSE)) {

        } else {
            webSocketSender.sendAutoControl("Wrong cmd", StateControl.WAITING);
        }

    }

    @Scheduled(fixedRate = 500)
    public void controlLoop() {
        if(!state.equals(StateControl.WAITINGRES)) {
            return;
        }
        RobotParam param =  connectedRobots.getParamRobot(currentMission.getRobotId(), currentCmd.getResponseTopic());
        
    }

    public void startMission() {
        if (currentMission == null) {
            String msg = "Need to set current mission";
            LOGGER.info(msg);
            webSocketSender.sendAutoControl(msg, StateControl.STOP);
            return;
        } 
        if (currentMission.getCmds().size() == 0) {
            String msg = "size of mission is 0, please check";
            LOGGER.info(msg);
            webSocketSender.sendAutoControl(msg, StateControl.STOP);
            return;
        }
        webSocketSender.sendAutoControl("Started new Mission: " + currentMission.getName() , StateControl.RUN);
        execCmd(currentMission.getCmds().get(0));
        currentMission.getCmds().remove(0);

    }

    public boolean injectNewMission(FileMeta newMission) {
        if (!state.equals(StateControl.STOP)) {
            webSocketSender.sendAutoControl("Failed add new Misison: " + newMission.getName() , StateControl.RUN);
            return false;
        }
        currentMission = newMission;
        return true;
    }

    public void execCmd(CmdElem cmd) {
        currentCmd = cmd;
        // TODO send cmd to topic 
        lastSend = LocalDateTime.now();
        this.state = StateControl.WAITINGRES;
        webSocketSender.sendAutoControl("Send cmd: " + cmd.getType() , StateControl.RUN);
    }


}
