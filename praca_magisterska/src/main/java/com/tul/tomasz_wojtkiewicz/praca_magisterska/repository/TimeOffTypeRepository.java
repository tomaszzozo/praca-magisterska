package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeOffTypeRepository extends JpaRepository<TimeOffTypeEntity, Long> {
    boolean existsByName(String name);
}
