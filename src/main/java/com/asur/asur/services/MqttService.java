package com.asur.asur.services;

import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;


import com.asur.asur.config.Mqtt;

@Component
public class MqttService {
    
    public MqttService() {
        try {
            subscribe("/asur/topic1", null);
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void subscribe(String name, IMqttMessageListener listener) throws MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        System.out.println("subscribe" + name);
        Mqtt.getInstance().subscribe(name,  (topic, msg) -> {
            byte[] payload = msg.getPayload();
            System.out.println("topic: " + topic + " msg: " + new String(payload));
            receivedSignal.countDown();
        });  
    }
    
}
