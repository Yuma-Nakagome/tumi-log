package com.example.tumi_log.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tumi_log.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserId(Long userId);

    List<Activity> findByUserIdAndArchiveFalse(Long userId);
}
