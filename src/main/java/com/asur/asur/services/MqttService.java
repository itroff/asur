package com.asur.asur.services;

import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import com.asur.asur.config.Mqtt;
import com.asur.asur.generated.model.Test;
import com.asur.asur.generated.model.Test.TestProto;

@Component
public class MqttService {

    public MqttService() {
        try {
            subscribe("asur/keepAlive");
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void subscribe(String name) throws MqttException {
        CountDownLatch receivedSignal = new CountDownLatch(10);
        System.out.println("subscribe: " + name);
        Mqtt.getInstance().subscribe(name, 1, (topic, msg) -> {

            byte[] payload = msg.getPayload();
            System.out.println(payload.length);
            //MqttMessage
            
            System.out.println("topic: " + topic + " msg: " + new String(payload));
            try {
                Test.TestProto proto = Test.TestProto.parseFrom(payload);
                System.out.println("Parsed: " + proto.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            receivedSignal.countDown();

        });
    }

}
