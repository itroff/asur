package com.asur.asur.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Robot {
    private int id;
    private String name;
    private String topicPrefix;
    private RobotStatusEnum status;
    
}
