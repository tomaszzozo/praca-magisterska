package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffRepository extends JpaRepository<TimeOffEntity, Long> {
    @Query("SELECT t FROM TimeOffEntity t WHERE t.employee.id = :accountId " +
            "AND YEAR(t.firstDay) = :leaveYear ")
    List<TimeOffEntity> findAllByYearAndEmployeeId(@Param("accountId") long accountId, @Param("leaveYear") int year);
}
