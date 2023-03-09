package com.asur.asur.models;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BooleanParam extends RobotParam{
    private boolean value;

    public BooleanParam(String name, boolean value){
        super(name, LocalDateTime.now());
        this.value = value;
    }

    @Override
    public void updateParam(RobotParam param){
        if(param.getParamName().equals(this.getParamName())) {
            BooleanParam p = (BooleanParam)param;
            setValue(p.isValue());
            this.setDt(p.getDt());
        }
    }
    
}
