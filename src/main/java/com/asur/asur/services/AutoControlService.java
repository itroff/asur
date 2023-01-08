package com.asur.asur.services;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.models.BoolCmd;
import com.asur.asur.models.BooleanParam;
import com.asur.asur.models.CargoCmd;
import com.asur.asur.models.CmdControl;
import com.asur.asur.models.CmdElem;
import com.asur.asur.models.FileMeta;
import com.asur.asur.models.IntegerParam;
import com.asur.asur.models.PosCmd;
import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotParam;
import com.asur.asur.models.StateControl;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Component
public class AutoControlService {
    // STATEs: WAITING, WAITINGRES, RUN, STOP, PAUSE
    // cmds: PLAY, PAUSE, STOP, cmdResponse
    //
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoControlService.class);

    @Autowired
    WebSocketSender webSocketSender;

    @Autowired
    ConnectedRobots connectedRobots;

    @Autowired
    MqttService mqttService;

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
            } else if (state.equals(StateControl.PAUSE)) {
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
            if (currentMission.getCmds().size() == 0) {
                state = StateControl.STOP;
                webSocketSender.sendAutoControl("Mission successfuly completed!", StateControl.STOP);
                if (currentMission.isRepeated()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        FileMeta file = mapper.readValue(Paths.get("/asur/asur/src/main/resources/scenarious", currentMission.getName() + ".json").toFile(), FileMeta.class);
                        injectNewMission(file);
                        controlStep(CmdControl.PLAY);
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                    }
                }
            } else {
                currentMission.getCmds().remove(0);
                execCmd(currentMission.getCmds().get(0));
            }

        } else {
            webSocketSender.sendAutoControl("Wrong cmd", StateControl.WAITING);
        }

    }

    @Scheduled(fixedRate = 500)
    public void controlLoop() {
        try {
            if (!state.equals(StateControl.WAITINGRES)) {
                return;
            }
            if (connectedRobots.getRobot(currentMission.getRobotId()) == null) {
                return;
            }
            RobotParam param = connectedRobots.getParamRobot(currentMission.getRobotId(),
                    currentCmd.getResponseTopic());

            if (param == null) {
                return;
            }
            if (param.getDt().isAfter(lastSend)) {
                if (currentCmd.getType().equals("Boolean")) {
                    BooleanParam bp = (BooleanParam) param;
                    if (bp.isValue()) {
                        controlStep(CmdControl.RESPONSE);
                    } else {
                        state = StateControl.STOP;
                        webSocketSender.sendAutoControl("Stop Mission failed", StateControl.STOP);
                    }
                } else if (currentCmd.getType().equals("PosCmd") || currentCmd.getType().equals("CargoCmd")) {
                    IntegerParam ip = (IntegerParam) param;
                    if (ip.getValue() == 3) {
                        controlStep(CmdControl.RESPONSE);
                    } // TODO if failure
                }
            }
        } catch (Exception ex) {
            LOGGER.info("error in sheduled task {}", ex.getStackTrace());
        }
    }

    public void startMission() {
        if (currentMission == null) {
            String msg = "Need to set current mission";
            // LOGGER.info(msg);
            webSocketSender.sendAutoControl(msg, StateControl.STOP);
            return;
        }
        if (currentMission.getCmds().size() == 0) {
            String msg = "size of mission is 0, please check";
            // LOGGER.info(msg);
            webSocketSender.sendAutoControl(msg, StateControl.STOP);
            return;
        }
        LOGGER.info("mission robot {}", currentMission.getRobotId());
        webSocketSender.sendAutoControl("Started new Mission: " + currentMission.getName(), StateControl.RUN);
        execCmd(currentMission.getCmds().get(0));
        // currentMission.getCmds().remove(0);

    }

    public boolean injectNewMission(FileMeta newMission) {
        if (!state.equals(StateControl.STOP)) {
            webSocketSender.sendAutoControl("Failed add new Misison: " + newMission.getName(), StateControl.RUN);
            return false;
        }
        currentMission = newMission;
        return true;
    }

    public void execCmd(CmdElem cmd) {
        currentCmd = cmd;
        if (currentCmd.getType().equals("Boolean")) {
            mqttService.sendBoolCmd(cmd.getBoolCmd());
        } else if (currentCmd.getType().equals("PosCmd")) {
            mqttService.sendPosCmd(currentCmd.getPosCmd());
        } else if (currentCmd.getType().equals("CargoCmd")) {
            mqttService.sendCargoCmd(currentCmd.getCargoCmd());
        } else {
            state = StateControl.STOP;
            webSocketSender.sendAutoControl("Wrong Cmd", StateControl.STOP);
            return;
        }
        lastSend = LocalDateTime.now();
        this.state = StateControl.WAITINGRES;
        webSocketSender.sendAutoControl("Send cmd: " + cmd.getType(), StateControl.RUN);
    }

    public void saveDefaultFile() {
        FileMeta file = new FileMeta();
        file.setName("Default");
        file.setRepeated(false);
        file.setRobotId("001");
        List<CmdElem> list = new ArrayList();
        CmdElem el = new CmdElem();
        el.setType("Boolean");
        el.setTopic("action/start/cmd");
        el.setResponseTopic("action/start/res");
        Robot rb = new Robot();
        rb.setTopicId("001");
        BoolCmd bcmd = new BoolCmd("/action/start/cmd", rb, true);
        el.setBoolCmd(bcmd);

        CmdElem el2 = new CmdElem();
        el2.setType("PosCmd");
        el2.setTopic("action/pos/cmd");
        el2.setResponseTopic("action/pos/res");
        PosCmd pcmd = new PosCmd(true, 3, new int[0], rb);
        el2.setPosCmd(pcmd);

        CmdElem el3 = new CmdElem();
        el3.setType("CargoCmd");
        el3.setTopic("action/cargo/cmd");
        el3.setResponseTopic("action/cargo/res");
        CargoCmd ccmd = new CargoCmd(true, true, rb);
        el3.setCargoCmd(ccmd);
        List<CmdElem> els = Arrays.asList(el, el2, el3); // List.of(el, el2, el3);
        file.setCmds(els);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        try {
            writer.writeValue(Paths.get("/asur/asur/src/main/resources/scenarious", "Default.json").toFile(), file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getScenarious() {
        // Paths.get("src/main/resources/scenarious").to
        List<String> scenariousFiles = new ArrayList<>();
        List<String> files = Stream.of(new File("/asur/asur/src/main/resources/scenarious").listFiles())
                .filter(file -> (!file.isDirectory() && file.getName().endsWith(".json")))
                .map(File::getName)
                .collect(Collectors.toList());
        for (String filename : files) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                LOGGER.info(filename);
                FileMeta mission = mapper.readValue(
                        Paths.get("/asur/asur/src/main/resources/scenarious", filename).toFile(), FileMeta.class);
                scenariousFiles.add(mission.getName());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        if (scenariousFiles.size() == 0) {
            saveDefaultFile();
            scenariousFiles.add("Default");
        }
        return scenariousFiles;

    }

}
