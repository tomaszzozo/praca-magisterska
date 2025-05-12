package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffLimitRepository extends JpaRepository<TimeOffLimitEntity, Long> {
    List<TimeOffLimitEntity> findAllByLeaveYearAndEmployeeId(int year, long employeeId);
    boolean existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(int year, long employeeId, long timeOffTypeId);
}
