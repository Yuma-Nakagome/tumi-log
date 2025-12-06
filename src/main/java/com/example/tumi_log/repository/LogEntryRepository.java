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

}