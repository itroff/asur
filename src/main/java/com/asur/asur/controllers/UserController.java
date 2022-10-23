package com.asur.asur.controllers;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.asur.asur.config.Mqtt;
import com.asur.asur.config.Robots;
import com.asur.asur.generated.model.Robot;
import com.asur.asur.generated.model.Test;
import com.asur.asur.generated.model.Robot.RobotProto;
import com.asur.asur.generated.model.Test.TestProto;

@Controller
public class UserController {


    private String getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "admin";
        } 
        return "user";
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @GetMapping({"/index", "/"})
    public String index(Model model) {
        model.addAttribute("role", getRole());
        model.addAttribute("username", getUsername());
     //   model.addAttribute("validScenario", false);
        return "main";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/diagnostics")
    public String diagnostics(Model model) {

        model.addAttribute("role", getRole());
        model.addAttribute("username", getUsername());
        return "diagnostics";
    }

    @GetMapping("/robot")
    public String robot(Model model) {
        model.addAttribute("role", getRole());
        model.addAttribute("username", getUsername());
        return "robot";
    }
    
/* 
    @GetMapping("/led")
    public String led(Model model, @RequestParam(name="num", required = false) int param) {
        if (param != 0) {
            Test.TestProto proto = TestProto.newBuilder().setNumBlink(param).build();
            MqttMessage msg = new MqttMessage();
            msg.setPayload(proto.toByteArray());
            System.out.println(proto.toString());
            System.out.println(proto.toByteArray());
            System.out.println(proto.toByteString());
            try {
                Mqtt.getInstance().publish("asur/led", msg);
            } catch (MqttException ex) {
                System.out.println(ex.getStackTrace());
            }
            
        }
        model.addAttribute("num", param);
        return "led";
    }*/

    
}
