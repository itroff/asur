package com.asur.asur.models;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileMeta {
    private String name;
    private String robotId;
    private List <CmdElem> cmds;
    private boolean repeated;    
}
