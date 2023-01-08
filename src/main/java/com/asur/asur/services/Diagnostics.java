package com.asur.asur.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asur.asur.models.DiagnosticElem;

@Component
public class Diagnostics {

    private final static Logger LOGGER = LoggerFactory.getLogger(Diagnostics.class);
    private final int SIZE_DEQUE = 1000;
    ArrayDeque<DiagnosticElem> obj;

    @Autowired
    WebSocketSender webSocketSender;

    public Diagnostics() {
        obj = new ArrayDeque<>(SIZE_DEQUE);// TOOD params
    }

    public synchronized void addElem(DiagnosticElem elem) {
     //   LOGGER.info(elem.toString());
        if (obj.size() > SIZE_DEQUE){
            obj.pop();
        }
        obj.add(elem);
    
        webSocketSender.sendDiagnostic(elem);
    }

    public List<DiagnosticElem> getDiagnostics() {
    //    LOGGER.info(obj.toString());
        return new ArrayList<>(obj);
    }
}
