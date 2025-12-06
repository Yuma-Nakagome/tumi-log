package com.example.tumi_log.entity;

import java.time.LocalDate;

import jakarta.persistence.*;// JPA関連
import lombok.*; // Lombokの主要なものを一括インポート (推奨)

// --- Lombok アノテーション ---
@Getter
@Setter
@NoArgsConstructor // 引数なしのコンストラクタを自動生成
@AllArgsConstructor // 全てのフィールドを持つコンストラクタを自動生成
@Builder // ビルダーパターンによるオブジェクト生成を可能にする（便利）
// --- JPA アノテーション ---
@Entity
@Table(name = "logs")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDの自動採番戦略を指定 (MySQLのAUTO_INCREMENTに対応)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "memo", nullable = true)
    private String memo;

}
