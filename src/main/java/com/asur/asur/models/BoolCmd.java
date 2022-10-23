package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoolCmd {
    private String topic;
    private Robot robot;
    private boolean value;
}
