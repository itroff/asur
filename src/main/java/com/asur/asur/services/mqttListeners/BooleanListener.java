package com.asur.asur.services.mqttListeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asur.asur.generated.model.Boolean;
import com.asur.asur.generated.model.Boolean.BooleanProto;
import com.asur.asur.models.BooleanParam;
import com.asur.asur.models.DiagnosticElem;
import com.asur.asur.services.ConnectedRobots;

//@Component
public class BooleanListener implements  IMqttMessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanListener.class);

    private ExecutorService pool = Executors.newFixedThreadPool(10);

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
            String[] splitterText = topic.split("/");
            String prefix = splitterText[1];
            try{
                Boolean.BooleanProto proto = BooleanProto.parseFrom(message.getPayload());
                String paramName = splitterText[2] + "/" + splitterText[3] + "/" + splitterText[4];
                LOGGER.info("topic: {}, msg: {}", topic, proto.toString());
                connectedRobots.updateParam(prefix, new BooleanParam(paramName, proto.getEnabled()),
                new DiagnosticElem(proto.toString(), topic, "Server"));
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        pool.execute(new MessageHandler(topic, message));
    }

    public BooleanListener(ConnectedRobots connectedRobots) {
        this.connectedRobots = connectedRobots;
    }

   /*  @Override
    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub
        
    }*/
    
}
