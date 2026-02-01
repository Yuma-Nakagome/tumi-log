package com.example.tumi_log.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tumi_log.controller.DuplicateUserException;
import com.example.tumi_log.dto.UserRegistrationDto;
import com.example.tumi_log.entity.User;
import com.example.tumi_log.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCryptなどのエンコーダーを想定

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    // 役割：メソッド内のDB操作を一連の作業として扱い、成功でコミット、失敗でロールバック（取り消し）する
    // UserはUserエンティティのこと DTOを引数に取る
    public User registerUser(UserRegistrationDto registrationDto) {

        // 1. ビジネスロジック：ユーザー名が重複していないかチェック
        if (userRepository.existsByUserName(registrationDto.getUserName())) {
            throw new DuplicateUserException("ユーザー名" + registrationDto.getUserName() + "は既に使用されています。");
        }

        // 2. DTOからエンティティへの変換とパスワードのハッシュ化（最も重要な処理）
        User newUser = new User();

        // DTOから安全なフィールドをコピー
        newUser.setUserName(registrationDto.getUserName());

        // パスワードのハッシュ化
        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        newUser.setPasswordHash(hashedPassword);

        // 3. データベースへの保存と結果の返却
        return userRepository.save(newUser);
    }
}
