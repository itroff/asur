package com.asur.asur.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosCmd {
    boolean direction;
    int position;
    int[] distancies;
    Robot robot;

    
}
