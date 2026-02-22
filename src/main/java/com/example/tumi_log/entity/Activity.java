package com.example.tumi_log.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor // 引数なしのコンストラクタを自動生成
@AllArgsConstructor // 全てのフィールドを持つコンストラクタを自動生成
@Builder // ビルダーパターンによるオブジェクト生成を可能にする（便利）
@Entity
@Table(name = "activities")
public class Activity {

    @Id // このフィールドが主キーであることを示す
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDの自動採番戦略を指定 (MySQLのAUTO_INCREMENTに対応)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Activity側: 複数（Many）のアクティビティが、User側: 一人（One）のユーザーに関連付けられる。
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "display_style", length = 100, nullable = true)
    private String displayStyle;

    // なぜ boolean（小文字）がいいのか？
    // Boolean（大文字）は初期値が null になります。
    // boolean（小文字）は初期値が false になります。
    // nullable = false の設定と非常に相性が良く、エラーが起きにくくなります。
    @Builder.Default // ビルダーパターンでオブジェクトを生成する際のデフォルト値を指定
    @Column(name = "archive", nullable = false)
    private boolean archive = false;
}
