package com.example.tumi_log.validation.validator;

import com.example.tumi_log.dto.UserRegistrationDto;
import com.example.tumi_log.validation.annotation.PasswordMatches;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationDto> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(UserRegistrationDto dto, ConstraintValidatorContext context) {
        // 1. DTO自体がnullの場合はスキップ（保険）
        if (dto == null) {
            return true;
        }

        // 2. パスワードのいずれかがnull、または一致しない場合はエラー
        if (dto.getPassword() == null || dto.getConfirmPassword() == null
                || !dto.getPassword().equals(dto.getConfirmPassword())) {
            // 3. エラーカスタマイズ
            context.disableDefaultConstraintViolation(); // デフォルトメッセージを無効化

            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
