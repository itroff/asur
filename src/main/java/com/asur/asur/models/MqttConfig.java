package com.asur.asur.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqttConfig {

    private String id;
    private String ip;
    private String port;
    private boolean connected = false;

    public String getUri() {
        return "tcp://" + ip + ":" + port;
    }
}
