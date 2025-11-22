package com.example.tumi_log.entity;

import jakarta.persistence.*; // JPA関連
import lombok.*; // Lombokの主要なものを一括インポート (推奨)

// --- Lombok アノテーション ---
@Getter
@Setter
@Builder
@NoArgsConstructor // ★★★ JPAのために必須！追加
@AllArgsConstructor
// --- JPA アノテーション ---
@Entity
@Table(name = "users")
public class User {

    // --- カラム定義 ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // BIGINT対応

    @Column(name = "userName", length = 100, nullable = false, unique = true)
    private String userName;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;
}