package com.asur.asur.services.mqttListeners;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asur.asur.generated.model.IntData;
import com.asur.asur.generated.model.IntData.IntDataProto;
import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.models.IntegerParam;
import com.asur.asur.services.ConnectedRobots;
import com.google.protobuf.util.JsonFormat;

public class IntDataListener  implements IMqttMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FloatDataListener.class);

    private ConnectedRobots connectedRobots;

    public IntDataListener(ConnectedRobots connectedRobots) {
        this.connectedRobots = connectedRobots;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //connectedRobots.updateParam(topic, null, null);
        String prefix = topic.split("/")[1];
        try{
            IntData.IntDataProto proto = IntDataProto.parseFrom(message.getPayload());
            String protoStr = JsonFormat.printer().includingDefaultValueFields().print(proto);
            String paramName = topic.split("/")[3];
            LOGGER.info("topic: {}, msg: {}", topic, protoStr);
            connectedRobots.updateParam(prefix, new IntegerParam(paramName, proto.getData()),
            new DiagnosticElem(protoStr, topic, "Server"));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            
        }
    }
    
}