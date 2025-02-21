package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOffRepository extends JpaRepository<TimeOffEntity, Long> {
    @Query("SELECT t FROM time_off_entity t WHERE t.account.id = :accountId " +
            "AND YEAR(t.startDate) = :year ")
    List<TimeOffEntity> findAllByYearAndEmployeeId(@Param("accountId") long accountId, @Param("year") int year);
}
