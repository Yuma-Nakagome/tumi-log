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
}
