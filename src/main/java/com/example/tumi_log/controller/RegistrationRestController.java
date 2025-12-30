package com.example.tumi_log.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tumi_log.dto.UserRegistrationDto;
import com.example.tumi_log.entity.User;
import com.example.tumi_log.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class RegistrationRestController {

    public final UserService userService;

    public RegistrationRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationDto> registerUser(
            @RequestBody @Validated UserRegistrationDto userRegistrationDto) {
        User registeredEntity = userService.registerUser(userRegistrationDto);
        // 2. 登録された Entity から DTO に必要なフィールドをコピーして変換する
        // ※ 実際には、Mapper (ModelMapper/MapStruct) を使うことが多い
        UserRegistrationDto responseDto = new UserRegistrationDto();
        responseDto.setId(registeredEntity.getId());
        responseDto.setUserName(registeredEntity.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // ★★★ 例外ハンドラーの追加 ★★★
    // ユーザー名重複エラー（DuplicateUserException）が発生したときに処理するメソッド
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateUserException ex) {

        // クライアント（JS）に返すエラー情報を格納するMap（JSONに変換される）を準備
        Map<String, String> errorResponse = new HashMap<>();

        // クライアントが判断するためのコードや定型メッセージをJSONのキー"error"に入れる
        errorResponse.put("error", "ユーザー名が重複しています");

        // Java側（UserService）で設定した詳細なエラーメッセージをJSONのキー"message"に入れる
        errorResponse.put("message", ex.getMessage());

        // HTTPステータス 409 Conflict を設定し、エラー情報(Map)をボディに含めて返す
        // (409は「競合エラー」を意味し、重複登録に最適なステータスコード)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
