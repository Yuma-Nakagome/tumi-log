package com.example.tumi_log.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.Collectors;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tumi_log.dto.LogEntryDto;
import com.example.tumi_log.entity.Activity;
import com.example.tumi_log.entity.LogEntry;
import com.example.tumi_log.entity.User;
import com.example.tumi_log.repository.ActivityRepository;
import com.example.tumi_log.repository.LogEntryRepository;
import com.example.tumi_log.repository.UserRepository;

@Service
public class LogEntryService {
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public LogEntryService(LogEntryRepository logEntryRepository,
            UserRepository userRepository,
            ActivityRepository activityRepository) {
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public LogEntryDto addLogEntry(LogEntryDto logEntryDto) {
        // 実装例: LogEntryDtoをLogEntryエンティティに変換し、保存する
        // ここでは詳細な実装は省略します
        LogEntry newlogEntry = new LogEntry();

        // User を取得してセット
        User user = userRepository.findById(logEntryDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("ユーザーが見つかりません。id:" + logEntryDto.getUserId()));
        newlogEntry.setUser(user);
        // Activity を取得してセット
        Activity activity = activityRepository.findById(logEntryDto.getActivityId())
                .orElseThrow(() -> new IllegalStateException("アクティビティが見つかりません。 id:" + logEntryDto.getActivityId()));
        newlogEntry.setActivity(activity);

        newlogEntry.setLogDate(logEntryDto.getLogDate());
        newlogEntry.setMemo(logEntryDto.getMemo());

        LogEntry savedEntry = logEntryRepository.save(newlogEntry);

        LogEntryDto response = new LogEntryDto();
        response.setId(savedEntry.getId());
        response.setUserId(savedEntry.getUser().getId());
        response.setActivityId(savedEntry.getActivity().getId());
        response.setLogDate(savedEntry.getLogDate());
        response.setMemo(savedEntry.getMemo());
        return response;
    }

    @Transactional
    public List<LogEntryDto> findByLogDate(Long userId, LocalDate logDate) {
        List<LogEntry> entries = logEntryRepository.findByUserIdAndLogDate(userId, logDate);

        return entries.stream().map(entry -> {
            LogEntryDto dto = new LogEntryDto();
            dto.setId(entry.getId());
            dto.setUserId(entry.getUser() != null ? entry.getUser().getId() : null);
            dto.setActivityId(entry.getActivity() != null ? entry.getActivity().getId() : null);
            dto.setLogDate(entry.getLogDate());
            dto.setMemo(entry.getMemo());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public LogEntryDto updateLog(LogEntryDto dto, Long id) {
        LogEntry updatedLog = logEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("アクティビティが見つかりません。ID: " + id));
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new IllegalStateException("アクティビティが見つかりません。 id:" + dto.getActivityId()));
        updatedLog.setActivity(activity);
        updatedLog.setLogDate(dto.getLogDate());
        updatedLog.setMemo(dto.getMemo());

        LogEntry savedLog = logEntryRepository.save(updatedLog);

        LogEntryDto updatedLogDto = new LogEntryDto();
        updatedLogDto.setId(savedLog.getId());
        updatedLogDto.setUserId(savedLog.getId());
        updatedLogDto.setActivityId(savedLog.getActivity().getId());
        updatedLogDto.setLogDate(savedLog.getLogDate());
        updatedLogDto.setMemo(savedLog.getMemo());
        return updatedLogDto;
    }

    @Transactional
    public void deleteLog(Long id) {
        logEntryRepository.deleteById(id);
    }

    /**
     * 指定された年月のログを全て取得する
     */
    // ★ LogEntryDto への変換ロジックを独立したプライベートメソッドに切り出し
    private LogEntryDto convertToDto(LogEntry entry) {
        LogEntryDto dto = new LogEntryDto();
        dto.setId(entry.getId());
        // 関連エンティティの安全なID取得とNullチェック
        dto.setUserId(entry.getUser() != null ? entry.getUser().getId() : null);
        dto.setActivityId(entry.getActivity() != null ? entry.getActivity().getId() : null);
        dto.setLogDate(entry.getLogDate());
        dto.setMemo(entry.getMemo());
        return dto;
    }

    // ★ メインのビジネスロジックメソッド
    @Transactional(readOnly = true) // 読み取り専用トランザクションを設定
    public List<LogEntryDto> findLogsByYearMonth(Long userId, int year, int month) {
        // 1. 期間計算ロジック
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 2. Repositoryからデータを取得 (セキュリティチェック込み)
        List<LogEntry> entries = logEntryRepository.findByUserIdAndLogDateBetween(userId, startDate, endDate);

        // 3. DTOに変換して返す
        return entries.stream()
                .map(this::convertToDto) // メソッド参照で簡潔に記述
                .collect(Collectors.toList());
    }
}