package com.asur.asur.services.mqttListeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.asur.asur.services.ConnectedRobots;

public class KeepAliveListener implements IMqttMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeepAliveListener.class);

    //@Autowired
    private ConnectedRobots connectedRobots;

    public KeepAliveListener(ConnectedRobots robots) {
        this.connectedRobots = robots;
    }
 
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    class MessageHandler implements Runnable {
        MqttMessage message;
        String topic;
        ConnectedRobots robots;



        public MessageHandler(String topic, MqttMessage message, ConnectedRobots robots) {
            this.message = message;
            this.topic = topic;
            this.robots = robots;
        }

        public void run() {
        //    LOGGER.info("Thread [ " + Thread.currentThread().getName() + 
        //    "], Topic[ "+ topic + "],  Message [" + message +"] ");
            robots.updateKeepAlive(topic);
            //System.out.println();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
     //   LOGGER.info("asdsad");
     //   LOGGER.info(connectedRobots.toString());
        pool.execute(new MessageHandler(topic, message, connectedRobots));
   /*      LOGGER.info("Thread [ " + Thread.currentThread().getName() + 
        "], Topic[ "+ topic + "],  Message [" + message +"] ");
        connectedRobots.updateKeepAlive(topic);*/
    }
    
}
