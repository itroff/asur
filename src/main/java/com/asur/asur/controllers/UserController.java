package com.asur.asur.controllers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.asur.asur.config.Mqtt;
import com.asur.asur.config.Robots;
import com.asur.asur.generated.model.Robot;
import com.asur.asur.generated.model.Test;
import com.asur.asur.generated.model.Robot.RobotProto;
import com.asur.asur.generated.model.Test.TestProto;

@Controller
public class UserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

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
    public String index(Model model, @RequestParam(name="file", required = false) String filename) {
        model.addAttribute("file", filename);
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
    
    @PostMapping("/put-file")
    public String putFile(@RequestParam("file") MultipartFile file,
    RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addAttribute("file", "Please select a file to upload.");
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            //TODO parsing json 

            Path path = Paths.get("/" + fileName); //TODO check dir
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            redirectAttributes.addAttribute("file", "Error while processing file");
            return "redirect:/";
        }

        redirectAttributes.addAttribute("file", file.getOriginalFilename() + " successfuly load");
        return "redirect:/";
    }

    
}
