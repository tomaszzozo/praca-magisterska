package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Tag("integration")
class TimeOffTypeRepositoryIntegrationTests {
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;

    @Test
    void fieldsOtherThanNameAreNotUnique() {
        assertDoesNotThrow(() -> timeOffTypeRepository.saveAllAndFlush(Map.ofEntries(Map.entry("Urlop wypoczynkowy", 1.0f), Map.entry("Urlop na żądanie", 1.0f), Map.entry("Urlop zdrowotny", 0.8f), Map.entry("Urlop szkoleniowy", 0.8f), Map.entry("Urlop rodzicielski", 0.6f), Map.entry("Urlop bo tak", 0.0f)).entrySet().stream().map(e -> TimeOffTypeTestEntityFactory.builder().name(e.getKey()).compensationPercentage(e.getValue()).build().asEntity()).toList()));
    }

    @Test
    void nameIsUnique() {
        var firstType = TimeOffTypeTestEntityFactory.build().asEntity();
        timeOffTypeRepository.saveAndFlush(firstType);
        var secondType = TimeOffTypeTestEntityFactory.builder().name(firstType.getName()).compensationPercentage(firstType.getCompensationPercentage() / 2f).build().asEntity();
        assertThrows(DataIntegrityViolationException.class, () -> timeOffTypeRepository.saveAndFlush(secondType));
    }

    @Test
    void timeOffLimitsRelationWorks() {
        var timeOffType = TimeOffTypeTestEntityFactory.build().asEntity();
        var limit = TimeOffLimitTestEntityFactory.builder().maxHours(89).build().asEntity();
        timeOffType.setYearlyLimits(List.of(limit));

        timeOffTypeRepository.saveAndFlush(timeOffType);

        var savedTimeOffType = timeOffTypeRepository.findById(timeOffType.getId()).orElseThrow();
        assertEquals(1, savedTimeOffType.getYearlyLimits().size());
        assertEquals(89, savedTimeOffType.getYearlyLimits().getFirst().getMaxHours());
    }
}
