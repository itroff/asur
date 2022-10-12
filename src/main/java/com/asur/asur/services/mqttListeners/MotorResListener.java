package com.asur.asur.services.mqttListeners;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotorResListener implements IMqttMessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(MotorResListener.class);

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOGGER.info("asdsad");
     //   LOGGER.info(connectedRobots.toString());
     //   pool.execute(new MessageHandler(topic, message, connectedRobots));
    }
}
