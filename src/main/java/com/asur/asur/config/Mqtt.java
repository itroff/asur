package com.asur.asur.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asur.asur.models.MqttConfig;
import com.asur.asur.services.MqttService;
import com.asur.asur.services.mqttListeners.KeepAliveListener;

@Component
@PropertySource("classpath:application.properties")
public class Mqtt {

    // private static final String MQTT_PUBLISHER_ID = "asur-server";
    // private static final String MQTT_SERVER_ADDRES=
    // "tcp://broker.hivemq.com:1883";//"tcp://127.0.0.1:1883";

    private static String SVN_URL = "";

    private static String MQTT_ID = "";

    @Value("${MQTT_ADDRESS}")
    public void setSvnUrl(String svnUrl) {
        SVN_URL = svnUrl;
    }

    @Value("${MQTT_ID}")
    public void setId(String id) {
        MQTT_ID = id;
    }
       
        
    private static final Logger LOGGER = LoggerFactory.getLogger(Mqtt.class);

    private static IMqttClient instance;
    private static MqttConfig config;

    //private static final String  localS = "127.0.0.1";

    private static final String  hivemqS = "broker.hivemq.com";

    public static IMqttClient getInstance() {
        try {
            if (instance == null) {
                Mqtt mqtt = new Mqtt();
                if(config == null) {
                    if (SVN_URL.isEmpty()) {
                        SVN_URL = "127.0.0.1";
                        LOGGER.info("empty url, use 127.0.0.1");
                    }
                    if (MQTT_ID.isEmpty()){
                        MQTT_ID = "asur-server";
                        LOGGER.info("use default id asur-server" );
                    }
                    config = new MqttConfig(MQTT_ID,  hivemqS, "1883", false);
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


    public Mqtt() {
        LOGGER.info("mqtt url {} id {}", SVN_URL, MQTT_ID);
    }
}
