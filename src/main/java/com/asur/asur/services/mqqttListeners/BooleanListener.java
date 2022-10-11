package com.asur.asur.services.mqqttListeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class BooleanListener implements  IMqttMessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanListener.class);

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    class MessageHandler implements Runnable {
        MqttMessage message;
        String topic;

        public MessageHandler(String topic, MqttMessage message) {
            this.message = message;
            this.topic = topic;
        }

        public void run() {
            LOGGER.info("Thread [ " + Thread.currentThread().getName() + 
            "], Topic[ "+ topic + "],  Message [" + message +"] ");
            //System.out.println();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        pool.execute(new MessageHandler(topic, message));
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
