package com.example.tumi_log.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class UserController {

    @GetMapping("/dashboard")
    public String checkIn() {
        return "dashboard";
    }

}
