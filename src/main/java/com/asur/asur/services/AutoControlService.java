package com.asur.asur.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.models.CmdControl;
import com.asur.asur.models.CmdElem;
import com.asur.asur.models.FileMeta;
import com.asur.asur.models.StateControl;

@Component
public class AutoControlService  {
    // STATEs: WAITING, WAITINGRES, RUN, STOP, PAUSE
    // cmds: PLAY, PAUSE, STOP, cmdResponse
    // 
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoControlService.class);

    @Autowired
    WebSocketSender webSocketSender;

    public AutoControlService() {
  
    }
    private FileMeta currentMission;

    private StateControl state = StateControl.STOP;
    private boolean isPaused = false;

    //@Scheduled(fixedRate = 500)
    public void controlStep(CmdControl cmd) {
        if (cmd.equals(CmdControl.STOP)) {
            state = StateControl.STOP;
            LOGGER.info("Stop Mission");
            webSocketSender.sendAutoControl("Stop Mission", StateControl.STOP);
        }
        switch (state) {
            case WAITING:
                if (cmd.equals(CmdControl.PLAY)) {
                    // send start next cmd
                }
                break;
            case WAITINGRES:
                if (cmd.equals(CmdControl.RESPONSE)) {
                    // check response and next cmd
                } 
                break;
            case STOP:
                if (cmd.equals(CmdControl.PLAY)) {
                    
                    // start new mission
                }
                break;
            case RUN: 
        }
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
        execCmd(currentMission.getCmds().get(0));

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
        // TODO send cmd to topic 
        this.state = StateControl.WAITINGRES;
        webSocketSender.sendAutoControl("Started new Mission: " + currentMission.getName() , StateControl.RUN);
    }

/*
    @Override
    public void run() {
        while(true) {
        LOGGER.info("thread");
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            LOGGER.error(ex.getMessage() );
        }
    }
        // TODO Auto-generated method stub
        
    }
    
    */
}
