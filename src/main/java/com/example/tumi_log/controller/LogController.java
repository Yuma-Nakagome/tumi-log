package com.example.tumi_log.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tumi_log.dto.LogEntryDto;
import com.example.tumi_log.service.LogEntryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/log")
public class LogController {
    // コントローラーの実装
    public final LogEntryService logEntryService;

    public LogController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @PostMapping
    public ResponseEntity<LogEntryDto> registerLog(@RequestBody @Validated LogEntryDto logEntryDto) {
        LogEntryDto registerLog = logEntryService.addLogEntry(logEntryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerLog);
    }

}
