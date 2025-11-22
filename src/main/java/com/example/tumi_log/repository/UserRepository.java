package com.example.tumi_log.repository;

import com.example.tumi_log.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Springコンポーネントとして登録
public interface UserRepository extends JpaRepository<User, Long> {

    // ★★★ ここからJPAが提供してくれるメソッドではない、カスタムメソッドの例 ★★★

    /**
     * ユーザー名に基づいてユーザーを検索するカスタムメソッド
     * 
     * @param userName 検索したいユーザー名
     * @return Optional<User> ユーザーが見つかれば格納、見つからなければOptional.empty()
     */
    Optional<User> findByUserName(String userName);

    // 同じユーザー名が登録されていないかチェック
    boolean existsByUserName(String userName);
}