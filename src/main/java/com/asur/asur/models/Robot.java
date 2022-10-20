package com.asur.asur.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Robot {
    private int id;
    private String name;
    private String topicId;
    private RobotStatusEnum status;
    @EqualsAndHashCode.Exclude
    private LocalDateTime lastKeepAlive;
    
}
