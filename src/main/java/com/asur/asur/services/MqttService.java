package com.asur.asur.services;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.scheduling.annotation.Scheduled;
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
           // System.out.println(payload.length);
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

    @Scheduled(fixedRate = 60 * 1000)
    public void sendLed() {
        Random random = new Random();
        int num = random.nextInt(4) + 1;
        Test.TestProto proto = TestProto.newBuilder().setNumBlink(num).build();
        MqttMessage msg = new MqttMessage();
        msg.setPayload(proto.toByteArray());
        System.out.println(proto.toString());
        //System.out.println(proto.toByteArray());
        //System.out.println(proto.toByteString());
        try {
            Mqtt.getInstance().publish("asur/led", msg);
        } catch (MqttException ex) {
            System.out.println(ex.getStackTrace());
        }
    }

}
