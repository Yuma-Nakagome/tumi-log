package com.example.tumi_log.controller;

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

    public final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

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
    public String registerUser(Model model) {
        model.addAttribute("registrationUser", new UserRegistrationDto());
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String registerUser(@Validated UserRegistrationDto registrationUser, BindingResult result, Model model) {
        System.out.println("POSTリクエストを受信しました。");
        if (result.hasErrors()) {
            System.out.println("バリデーションエラーが発生しました。フォームに戻ります。");
            return "registerUser";
        }
        System.out.println("バリデーションエラーはありませんでした。登録処理に進みます。");
        try {
            userService.registerUser(registrationUser);
        } catch (Exception e) {
            System.out.println("登録処理中に例外が発生しました: " + e.getMessage());
            result.rejectValue("userName", "duplicate", "そのユーザー名は既に使用されています。");
            model.addAttribute("errorMessage", "そのユーザー名は既に使用されています。");
            return "registerUser";
        }
        System.out.println("登録成功。ログイン画面にリダイレクトします。");
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