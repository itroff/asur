package com.asur.asur.services;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.config.Mqtt;
import com.asur.asur.generated.model.Boolean;
import com.asur.asur.generated.model.Test;
import com.asur.asur.generated.model.Boolean.BooleanProto;
import com.asur.asur.generated.model.PosCmd.PosCmdProto;
import com.asur.asur.generated.model.Test.TestProto;
import com.asur.asur.models.CargoCmd;
import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.models.MotorCmd;
import com.asur.asur.models.PosCmd;
import com.asur.asur.models.Robot;
import com.asur.asur.services.mqttListeners.BooleanListener;
import com.asur.asur.services.mqttListeners.KeepAliveListener;
import com.google.protobuf.GeneratedMessageV3;

@Component
public class MqttService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttService.class);
    public static final String prefix = "Robot/"; 

    @Autowired 
    private WebSocketSender webSocketSender;

   // @Autowired
   private ConnectedRobots connectedRobots;

    @Autowired
    private Diagnostics diagnostics;

    @Autowired
    public MqttService(ConnectedRobots connectedRobots) {
        this.connectedRobots = connectedRobots;
        try {
            Mqtt.getInstance().subscribe(prefix + "+/info/KeepAlive", 0, new KeepAliveListener(connectedRobots));
        } catch (MqttException ex) {
            LOGGER.error(ex.toString());
           // ex.printStackTrace();
        }
    }

    public void subscribe(String name) throws MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        //System.out.println("subscribe: " + name);
        LOGGER.info("subscribe: " + name);
        Mqtt.getInstance().subscribe(name, 1, (topic, msg) -> {

            byte[] payload = msg.getPayload();
           // System.out.println(payload.length);
            //MqttMessage
            
         //  System.out.println("topic: " + topic + " msg: " + new String(payload));
            LOGGER.info("topic: " + topic + " msg: " + new String(payload));
            try {
                Test.TestProto proto = Test.TestProto.parseFrom(payload);
                LOGGER.info("topic: " + topic + " msg: " + new String(payload));
                //System.out.println("Parsed: " + proto.toString());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                
            }

            receivedSignal.countDown();

        });
    }

 //   @Scheduled(fixedRate = 60 * 1000)
    public void sendLed() {
        Random random = new Random();
        int num = random.nextInt(4) + 1;
        Test.TestProto proto = TestProto.newBuilder().setNumBlink(num).build();
        System.out.println(proto.toString());
        //System.out.println(proto.toByteArray());
        //System.out.println(proto.toByteString());
        sendCmd("asur/led", proto);
    }

        
    @Scheduled(fixedRate = 5000)
    private void sendConfig() {
        try{
            webSocketSender.sendConfig(Mqtt.getConfig());
            Mqtt.getInstance().subscribe(prefix + "+/info/keepAlive", 0, new KeepAliveListener(connectedRobots));
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
    }

    public void sendBoolCmd(Robot robot, String cmd){
        Boolean.BooleanProto proto = BooleanProto.newBuilder().setEnabled(true).build();
        sendCmd(prefix + robot.getTopicId() + cmd, proto);
    }

    public void sendPosCmd(PosCmd cmd){
        com.asur.asur.generated.model.PosCmd.PosCmdProto proto = com.asur.asur.generated.model.PosCmd.PosCmdProto.newBuilder()
        .setDirection(cmd.isDirection()).setPosition(cmd.getPosition()).build();
        sendCmd(prefix + cmd.getRobot().getTopicId() + "/action/pos/cmd", proto);
    }

    public void sendCargoCmd(CargoCmd cmd){
        com.asur.asur.generated.model.CargoCmd.CargoCmdProto proto = com.asur.asur.generated.model.CargoCmd.CargoCmdProto.newBuilder()
        .setType(cmd.isType()).setDirection(cmd.isDirection()).build();
        sendCmd(prefix + cmd.getRobot().getTopicId() + "/action/cargo/cmd", proto);
    }

    public void sendMotorCmd(MotorCmd cmd){
        com.asur.asur.generated.model.MotorCmd.MotorCmdProto proto = com.asur.asur.generated.model.MotorCmd.MotorCmdProto.newBuilder()
        .setDirection(cmd.isDirection()).setSpeed(cmd.getSpeed()).build();
        sendCmd(prefix + cmd.getRobot().getTopicId() + "/motor/"+ cmd.getTopic() + "/cmd", proto);
    }

    public void sendCmd(String topic, GeneratedMessageV3 proto) {
        try {
            MqttMessage msg = new MqttMessage();
            msg.setPayload(proto.toByteArray());
            msg.setQos(2);
            Mqtt.getInstance().publish(topic,  msg);
            diagnostics.addElem(new DiagnosticElem(proto.toString(), "Server", topic));
        } catch(MqttException ex) {
             LOGGER.error(ex.getMessage());
         }
    }


}
