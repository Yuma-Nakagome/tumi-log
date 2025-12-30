package com.example.tumi_log.service;

import org.springframework.stereotype.Service;

import com.example.tumi_log.dto.LogEntryDto;
import com.example.tumi_log.entity.Activity;
import com.example.tumi_log.entity.LogEntry;
import com.example.tumi_log.entity.User;
import com.example.tumi_log.repository.ActivityRepository;
import com.example.tumi_log.repository.LogEntryRepository;
import com.example.tumi_log.repository.UserRepository;

import jakarta.transaction.Transactional;

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
}
