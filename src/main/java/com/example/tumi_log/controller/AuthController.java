package com.example.tumi_log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.tumi_log.dto.UserRegistrationDto;
import com.example.tumi_log.service.UserService;

@Controller
public class AuthController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/login", params = "error")
    public String loginFail(Model model) {
        model.addAttribute("errorMessage", "ユーザー名またはパスワードが違います");
        return "login";
    }

    @GetMapping("/registerUser")
    public String registerUser() {
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String registerUser(@Validated UserRegistrationDto registrationUser, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "registerUser";
        }

        try {
            userService.registerUser(registrationUser);
        } catch (Exception e) {
            result.rejectValue("userName", "duplicate", "そのユーザー名は既に使用されています。");
            model.addAttribute("errorMessage", "そのユーザー名は既に使用されています。");
            return "registerUser";
        }

        return "redirect:/login";

    }

    // @GetMapping("/responseSample")
    // public String responseSample(Model model) { // Modelを引数に追加
    // // userオブジェクトが存在しない場合に備えて、null または空のオブジェクトを追加
    // if (!model.containsAttribute("user")) {
    // model.addAttribute("user", new UserRegistrationDto()); // nullチェック回避
    // }
    // return "responseSample";
    // }

}