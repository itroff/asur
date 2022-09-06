package com.asur.asur.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("val", "mer");
        return "index";
    }
}
