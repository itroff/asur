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


    @GetMapping("/index")
    public String index(Model model) {
       // model.addAttribute("val", "mer");
        //TODO role as param
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("role", "admin");
        } else {
            model.addAttribute("role", "user");
        }
       // Robot.RobotProto proto = RobotProto.newBuilder().setId(1).setRobotName("val123").build();
      
        model.addAttribute("robots", Robots.getRobots());
        model.addAttribute("validScenario", false);
        return "main";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("val", "mer");
        return "login";
    }


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
    }
    

    @GetMapping("/ws")
    public String ws() {
        return "ws";
    }
    
}
