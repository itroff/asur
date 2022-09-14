package com.asur.asur.controllers;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.asur.asur.config.Mqtt;

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
        return "main";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("val", "mer");
        return "login";
        
    }

    
}
