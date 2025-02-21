package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeLimitPerYearAndEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffTypeLimitPerYearAndEmployeeRepository extends JpaRepository<TimeOffTypeLimitPerYearAndEmployeeEntity, Long> {
    List<TimeOffTypeLimitPerYearAndEmployeeEntity> findAllByYearAndEmployeeId(int year, long employeeId);
    boolean existsByYearAndEmployeeIdAndTimeOffTypeId(int year, long employeeId, long timeOffTypeId);
}
