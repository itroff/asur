package com.asur.asur.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.asur.asur.models.MqttConfig;
import com.asur.asur.services.MqttService;
import com.asur.asur.services.mqttListeners.KeepAliveListener;

public class Mqtt {

    // private static final String MQTT_PUBLISHER_ID = "asur-server";
    // private static final String MQTT_SERVER_ADDRES=
    // "tcp://broker.hivemq.com:1883";//"tcp://127.0.0.1:1883";

    private static final Logger LOGGER = LoggerFactory.getLogger(Mqtt.class);

    private static IMqttClient instance;
    private static MqttConfig config;

    public static IMqttClient getInstance() {
        try {
            if (instance == null) {
                if(config == null) {
                    config = new MqttConfig("asur-server", "127.0.0.1"/*"broker.hivemq.com"*/, "1883", false);
                }
                LOGGER.info(config.toString());
                // TODO init config
                instance = new MqttClient(config.getUri(), config.getId());
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
        //    options.setConnectionTimeout(10);

            if (!instance.isConnected()) {
                instance.connect(options);
                
            }
        } catch (MqttException e) {
            LOGGER.error("getInstance");
            LOGGER.error(e.toString());
            // se.printStackTrace();
        }

        return instance;
    }

    public static void reconnect(MqttConfig configNew) {
        LOGGER.info(configNew.toString());
        config = configNew;
        
        try {
            instance.disconnectForcibly();
            //instance.close();
            instance = null;

            getInstance();
        } catch (MqttException e) {
            LOGGER.error("reconnect");
            LOGGER.error(e.toString());
        }
    }

    public static MqttConfig getConfig(){ 
        config.setConnected(getInstance().isConnected());
        return config;
    }


    private Mqtt() {

    }
}
