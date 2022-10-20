package com.asur.asur.models;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IntegerParam extends RobotParam {
    private int value;
    public IntegerParam(String name, int value){
        super(name, LocalDateTime.now());
        this.value = value;
    }

    @Override
    public void updateParam(RobotParam param){
        if(param.getParamName().equals(this.getParamName())) {
            IntegerParam p = (IntegerParam)param;
            setValue(p.getValue());
            this.setDt(p.getDt());
        }
    }
}
