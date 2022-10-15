package com.asur.asur.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiagnosticElem {
    private LocalDateTime dt;
    private String msg;
    private String src;
    private String dst;

    public DiagnosticElem(String msg, String src, String dst) {
        this.dt = LocalDateTime.now();
        this.msg = msg;
        this.src = src;
        this.dst = dst;
    }
}
