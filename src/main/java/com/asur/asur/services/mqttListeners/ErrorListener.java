package com.asur.asur.services.mqttListeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asur.asur.generated.model.Error.ErrorProto;
import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.models.IntegerParam;
import com.asur.asur.services.ConnectedRobots;
import com.google.protobuf.util.JsonFormat;

public class ErrorListener implements IMqttMessageListener {

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorListener.class);

    private ConnectedRobots connectedRobots;

    class MessageHandler implements Runnable {
        MqttMessage message;
        String topic;

        public MessageHandler(String topic, MqttMessage message) {
            this.message = message;
            this.topic = topic;
        }

        public void run() {
          //  LOGGER.info("Thread [ " + Thread.currentThread().getName() + 
        //    "], Topic[ "+ topic + "],  Message [" + message +"] ");
            String prefix = topic.split("/")[1];
            try{
                com.asur.asur.generated.model.Error.ErrorProto proto = ErrorProto.parseFrom(message.getPayload());
                String protoStr = JsonFormat.printer().includingDefaultValueFields().print(proto);
                String paramName = topic.split("/")[3];
                LOGGER.info("topic: {}, msg: {}", topic, protoStr);
                connectedRobots.updateParam(prefix, new IntegerParam(paramName, proto.getError()),
                new DiagnosticElem(protoStr, topic, "Server"));
                connectedRobots.updateRobotError(prefix, proto.getError());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        pool.execute(new MessageHandler(topic, message));
    }

    public ErrorListener(ConnectedRobots connectedRobots) {
        this.connectedRobots = connectedRobots;
    }
}
