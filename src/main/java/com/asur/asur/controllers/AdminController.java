package com.asur.asur.controllers;

import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.asur.asur.config.Robots;
import com.asur.asur.models.Robot;

@Controller
public class AdminController {

    @GetMapping("/robot")
    public String robot(Model model, @RequestParam(name="id", required = false) int param) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("role", "admin");
        } else {
            model.addAttribute("role", "user");
        }
        if (param != 0) {
            Robot robot = Robots.getRobot(param);
            if (robot != null) {
                model.addAttribute("robot", robot);
            }
        }
        model.addAttribute("robots", Robots.getRobots());

        return "robot";
    }


}
