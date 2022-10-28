package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CmdElem {
    private String topic;
    private String type;
    private String responseTopic;
    private String responseType;
    
}
