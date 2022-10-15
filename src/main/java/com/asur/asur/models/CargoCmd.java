package com.asur.asur.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CargoCmd {
    private boolean type;
    private boolean direction;
    private Robot robot;
}
