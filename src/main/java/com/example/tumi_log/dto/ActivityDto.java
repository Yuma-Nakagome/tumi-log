package com.example.tumi_log.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityDto {
    private Long id;

    // ★ Userエンティティではなく、ユーザーIDのみにする
    private Long userId;

    @NotBlank
    @Size(min = 1, max = 30, message = "タイトルは30字以内で入力してください。")
    private String title;

    @NotBlank
    @Size(min = 1, max = 30, message = "アイコンは1〜30字以内で入力してください。")
    private String displayStyle;
}
