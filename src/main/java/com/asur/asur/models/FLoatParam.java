package com.asur.asur.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FLoatParam  extends RobotParam {
    private float value;

    public FLoatParam(String name, float value) {
        super(name, LocalDateTime.now());
        this.value = value;
    }

    @Override
    public void updateParam(RobotParam param){
        if(param.getParamName().equals(this.getParamName())) {
            FLoatParam p = (FLoatParam)param;
            setValue(p.getValue());
            this.setDt(p.getDt());
        }
    }
}
