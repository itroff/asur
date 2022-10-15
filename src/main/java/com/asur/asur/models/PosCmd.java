package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PosCmd {
    boolean direction;
    int position;
    float[] distancies;
    Robot robot;
}
