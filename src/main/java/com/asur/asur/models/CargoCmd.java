package com.asur.asur.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoCmd {
    private boolean type;
    private boolean direction;
    private Robot robot;
}
