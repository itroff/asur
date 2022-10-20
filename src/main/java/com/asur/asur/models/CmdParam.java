package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CmdParam {
    private String topic;
    private float value;
    private Robot robot;
    
}
