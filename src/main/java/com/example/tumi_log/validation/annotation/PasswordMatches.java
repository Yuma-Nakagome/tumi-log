package com.example.tumi_log.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// ★ 必須要素の追加
@Target({ TYPE }) // このアノテーションはクラスに付けられることを指定
@Retention(RUNTIME) // 実行時に利用可能であることを指定
@Constraint(validatedBy = com.example.tumi_log.validation.validator.PasswordMatchesValidator.class)
@Documented // (オプションだが推奨)
public @interface PasswordMatches { // ★ public @interface に変更

    // 必須: エラーメッセージ
    String message() default "パスワードと確認用パスワードが一致しません。";

    // 必須: グループ化のための要素
    Class<?>[] groups() default {};

    // 必須: ペイロードのための要素
    Class<? extends Payload>[] payload() default {};
}
