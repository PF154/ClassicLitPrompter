package com.writingprompt.backend.repository;

import com.writingprompt.backend.entity.DailyResponseCount;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyResponseCountRepository extends JpaRepository<DailyResponseCount, Long>{
    Optional<DailyResponseCount> findByIpAddressAndResponseDate(String ipAddress, LocalDate responseDate);
}
