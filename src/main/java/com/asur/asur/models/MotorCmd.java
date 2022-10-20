package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MotorCmd {
    private String topic;
    private Robot robot;
    private boolean direction;
    private int speed;
}
