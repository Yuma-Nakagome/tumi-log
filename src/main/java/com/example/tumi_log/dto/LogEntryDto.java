package com.example.tumi_log.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogEntryDto {

    private Long id;

    private Long userId;
    @NotNull
    private Long activityId;

    @NotNull
    private LocalDate logDate;

    private String memo;

}
