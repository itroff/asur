package com.asur.asur.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Robot {
    private int id;
    private String name;
    private String topicId;
    private RobotStatusEnum status;
    private LocalDateTime lastKeepAlive;
    
}
