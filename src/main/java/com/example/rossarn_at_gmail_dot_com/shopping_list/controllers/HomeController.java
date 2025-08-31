package com.example.rossarn_at_gmail_dot_com.shopping_list.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String displayHome(Model model){
        return "index";

    }
}
