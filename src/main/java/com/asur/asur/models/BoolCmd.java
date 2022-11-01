package com.asur.asur.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoolCmd {
    private String topic;
    private Robot robot;
    private boolean value;
}
