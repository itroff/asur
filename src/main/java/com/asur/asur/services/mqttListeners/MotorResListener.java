package com.asur.asur.services.mqttListeners;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asur.asur.generated.model.MotorRes;
import com.asur.asur.generated.model.MotorRes.MotorResProto;
import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.models.IntegerParam;
import com.asur.asur.services.ConnectedRobots;

public class MotorResListener implements IMqttMessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(MotorResListener.class);

    private ConnectedRobots connectedRobots;

    public MotorResListener(ConnectedRobots connectedRobots) {
        this.connectedRobots = connectedRobots;
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //connectedRobots.updateParam(topic, null, null);
        String[] splitterText = topic.split("/");
        String prefix = splitterText[1];
        try{
            MotorRes.MotorResProto proto = MotorResProto.parseFrom(message.getPayload());
            String paramName =  splitterText[2] + "/" + splitterText[3] + "/" + splitterText[4];
            LOGGER.info("topic: {}, msg: {}", topic, proto.toString());
            connectedRobots.updateParam(prefix, new IntegerParam(paramName, proto.getStatus()),
            new DiagnosticElem(proto.toString(), topic, "Server"));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            
        }
    
     //   LOGGER.info("asdsad");
     //   LOGGER.info(connectedRobots.toString());
     //   pool.execute(new MessageHandler(topic, message, connectedRobots));
    }
}
