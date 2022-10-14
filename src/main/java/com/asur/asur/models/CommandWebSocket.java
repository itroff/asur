package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommandWebSocket {
    private String cmd;
    private Robot robot;
    
}
