package com.example.tumi_log.repository;

import com.example.tumi_log.entity.LogEntry;
import com.example.tumi_log.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository // Springコンポーネントとして登録
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByUserId(User user);

    List<LogEntry> findByLogDate(LocalDate logDate);

    List<LogEntry> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    /**
     * Spring Data JPAの命名規則による期間検索
     * LogDate が startDate と endDate の間にある全てのレコードを取得
     */
    List<LogEntry> findByUserIdAndLogDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}