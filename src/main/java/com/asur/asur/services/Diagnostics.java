package com.asur.asur.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asur.asur.models.DiagnosticElem;

@Component
public class Diagnostics {

    private final static Logger LOGGER = LoggerFactory.getLogger(Diagnostics.class);
    ArrayDeque<DiagnosticElem> obj;

    @Autowired
    WebSocketSender webSocketSender;

    public Diagnostics() {
        obj = new ArrayDeque<>(100);
    }

    public void addElem(DiagnosticElem elem) {
        LOGGER.info(elem.toString());
        obj.add(elem);
        webSocketSender.sendDiagnostic(elem);
    }

    public List<DiagnosticElem> getDiagnostics() {
        LOGGER.info(obj.toString());
        return new ArrayList<>(obj);
    }
}
