package com.example.tumi_log.dto;

import com.example.tumi_log.validation.annotation.PasswordMatches;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@PasswordMatches
public class UserRegistrationDto {

    private Long id;
    @NotBlank
    @Size(min = 1, max = 20, message = "ユーザー名は1〜20字以内で入力してください。")
    private String userName;

    @NotBlank
    @Size(min = 8, max = 30, message = "パスワードは8〜30字以内で入力してください。")
    private String password;

    @NotBlank
    @Size(min = 8, max = 30, message = "パスワードは8〜30字以内で入力してください。")
    private String confirmPassword;

}
