package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeLimitPerYearAndEmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

@SpringBootTest
class TimeOffTypeLimitPerYearAndEmployeeEntityTests {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffTypeLimitPerYearAndEmployeeRepository timeOffTypeLimitPerYearAndEmployeeRepository;

    private EmployeeEntity testEmployee;
    private TimeOffTypeEntity testType;
    private final Supplier<TimeOffTypeLimitPerYearAndEmployeeEntity> testLimit = () -> DefaultTestObjects.getLimitEntity(testType, testEmployee);

    @BeforeEach
    void beforeEach() {
        testEmployee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(testEmployee);
        testType = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(testType);
    }

    @AfterEach
    void afterEach() {
        timeOffTypeLimitPerYearAndEmployeeRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void maxHoursMoreThenHoursInYearValidation() {
        for (int y = 2025; y < 2050; y++) {
            var limit = testLimit.get();
            limit.setLeaveYear(y);
            limit.setMaxHours(LocalDate.of(y, 12, 31).getDayOfYear() * 24 + 1);
            Assertions.assertThrows(jakarta.validation.ValidationException.class, () -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
            limit.setMaxHours(LocalDate.of(y, 12, 31).getDayOfYear() * 24);
            Assertions.assertDoesNotThrow(() -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
            timeOffTypeLimitPerYearAndEmployeeRepository.deleteAll();
        }
    }

    @Test
    void negativeAndZeroMaxHoursValidation() {
        var limit = testLimit.get();
        List.of(Integer.MIN_VALUE, -100, -1).forEach(h -> {
            limit.setMaxHours(h);
            Assertions.assertThrows(jakarta.validation.ValidationException.class, () -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
        });
        limit.setMaxHours(0);
        Assertions.assertDoesNotThrow(() -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
    }

    @Test
    void leaveYearValidation() {
        List.of(Integer.MIN_VALUE, -1000, -1, 0, 1, 100, 2000, 2019, 2101, 2222222, Integer.MAX_VALUE).forEach(y -> {
            var limit = testLimit.get();
            limit.setLeaveYear(y);
            Assertions.assertThrows(jakarta.validation.ValidationException.class, () -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
        });
        for (int y = 2020; y <= 2100; y++) {
            var limit = testLimit.get();
            limit.setLeaveYear(y);
            Assertions.assertDoesNotThrow(() -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
            timeOffTypeLimitPerYearAndEmployeeRepository.deleteAll();
        }
    }

    @Test
    void notNullFieldsValidation() {
        {
            var limit = testLimit.get();
            limit.setEmployee(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
        }
        {
            var limit = testLimit.get();
            limit.setTimeOffType(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffTypeLimitPerYearAndEmployeeRepository.save(limit));
        }
    }

    @Test
    void validData() {
        var employees = ValidDataProvider.getEmployees();
        employeeRepository.saveAll(employees);
        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);
        Assertions.assertDoesNotThrow(() -> timeOffTypeLimitPerYearAndEmployeeRepository.saveAll(ValidDataProvider.getTimeOffLimits(employees, types)));
    }
}
