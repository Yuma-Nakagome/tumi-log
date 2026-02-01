package com.example.tumi_log.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tumi_log.dto.LogEntryDto;
import com.example.tumi_log.repository.UserRepository;
import com.example.tumi_log.service.CustomUserDetails;
import com.example.tumi_log.service.LogEntryService;

import java.util.List;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    // コントローラーの実装
    public final LogEntryService logEntryService;

    public LogController(LogEntryService logEntryService, UserRepository userRepository) {
        this.logEntryService = logEntryService;
    }

    @PostMapping
    public ResponseEntity<LogEntryDto> registerLog(@RequestBody @Validated LogEntryDto logEntryDto) {
        LogEntryDto registerLog = logEntryService.addLogEntry(logEntryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerLog);
    }

    @GetMapping("/date")
    public ResponseEntity<List<LogEntryDto>> getLogDate(@AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = true) LocalDate date) {
        Long userId = principal.getId();
        List<LogEntryDto> foundLogs = logEntryService.findByLogDate(userId, date);
        return ResponseEntity.status(HttpStatus.OK).body(foundLogs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogEntryDto> putLog(@RequestBody @Validated LogEntryDto logEntryDto, @PathVariable Long id) {
        LogEntryDto putLog = logEntryService.updateLog(logEntryDto, id);
        return ResponseEntity.ok(putLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        logEntryService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/month")
    public ResponseEntity<List<LogEntryDto>> getMonthlyLogs(
            // Spring Securityから認証ユーザー情報を取得
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam int year,
            @RequestParam int month) {

        // 1. 認証情報からユーザーIDを取得する安全なロジック
        Long userId;
        try {
            // ★ 実際に使う際は、principalを独自のCustomUserDetailsにキャストしてIDを取得してください
            userId = principal.getId();
        } catch (Exception e) {
            // IDが取得できない場合は、ログイン中の問題として403 Forbiddenなどを返す
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 2. Serviceを呼び出し、データ（DTOリスト）を取得
        List<LogEntryDto> monthlyLogs = logEntryService.findLogsByYearMonth(userId, year, month);

        // 3. 200 OK でデータを返す（最も簡潔な記述）
        return ResponseEntity.ok(monthlyLogs);
    }

}