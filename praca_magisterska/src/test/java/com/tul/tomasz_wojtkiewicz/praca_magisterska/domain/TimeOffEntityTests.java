package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeLimitPerYearAndEmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
class TimeOffEntityTests {
    @Autowired
    private TimeOffRepository timeOffRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffTypeLimitPerYearAndEmployeeRepository timeOffTypeLimitPerYearAndEmployeeRepository;

    private TimeOffTypeLimitPerYearAndEmployeeEntity testLimit;

    @BeforeEach
    void beforeEach() {
        var testEmployee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(testEmployee);
        var testType = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(testType);
        testLimit = DefaultTestObjects.getLimitEntity(testType, testEmployee);
        timeOffTypeLimitPerYearAndEmployeeRepository.save(testLimit);
    }

    @AfterEach
    void afterEach() {
        timeOffRepository.deleteAll();
        timeOffTypeLimitPerYearAndEmployeeRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void negativeHoursCountValidation() {
        List.of(Integer.MIN_VALUE, -1000, -1).forEach(h -> {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setHoursCount(h);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        });
        var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
        timeOff.setHoursCount(0);
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstDayAfterLastDayValidation() {
        List.of(1, 5, 10, 30).forEach(d -> {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setLastDayInclusive(LocalDate.now());
            timeOff.setFirstDay(timeOff.getLastDayInclusive().plusDays(d));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        });
        var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
        timeOff.setFirstDay(LocalDate.now());
        timeOff.setLastDayInclusive(timeOff.getFirstDay());
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstAndLastDayYearRangeValidation() {
        List.of(LocalDate.MIN.getYear(), -123456789, -12345678, -1234567, -123456, -12345, -1234, -123, -1, 0, 1, 123, 1234, 2000, 2019, 2101, 2200, 12345, 123456, 1234567, 12345678, 123456789, LocalDate.MAX.getYear()).forEach(y -> {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            var timeOff2 = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setFirstDay(LocalDate.of(y, 1, 1));
            timeOff2.setLastDayInclusive(LocalDate.of(y, 1, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        });
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setFirstDay(LocalDate.of(2020, 1, 1));
            timeOff.setLastDayInclusive(timeOff.getFirstDay());
            Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
            timeOffRepository.deleteAll();
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setFirstDay(LocalDate.of(2100, 12, 31));
            timeOff.setLastDayInclusive(timeOff.getFirstDay());
            Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
            timeOffRepository.deleteAll();
        }

    }

    @Test
    void firstAndLastDayMonthAndYearMatchValidation() {
        for (int i = 2; i <= 12; i++) {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setLastDayInclusive(LocalDate.of(2025, 1, 1));
            timeOff.setFirstDay(LocalDate.of(2025, i, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        for (int i = 2021; i < 2100; i++) {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setFirstDay(LocalDate.of(2020, 1, 1));
            timeOff.setLastDayInclusive(LocalDate.of(i, 1, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
    }

    @Test
    void hoursCountNotMoreThanHoursInTimeOffPeriodValidation() {
        var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
        timeOff.setHoursCount((int) timeOff.getFirstDay().until(timeOff.getLastDayInclusive().plusDays(1), ChronoUnit.DAYS) * 24 + 1);
        Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        timeOff.setHoursCount((int) timeOff.getFirstDay().until(timeOff.getLastDayInclusive().plusDays(1), ChronoUnit.DAYS) * 24);
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void notNullFieldsValidation() {
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setComment(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setFirstDay(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setLastDayInclusive(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setTimeOffYearlyLimit(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setTimeOffType(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setEmployee(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
    }

    @Test
    void employeeFirstDayAndEmployeeLastDayUniqueness() {
        timeOffRepository.save(DefaultTestObjects.getTimeOffEntity(testLimit));
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setLastDayInclusive(timeOff.getLastDayInclusive().plusDays(1));
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = DefaultTestObjects.getTimeOffEntity(testLimit);
            timeOff.setFirstDay(timeOff.getFirstDay().minusDays(1));
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff));
        }
    }

    @Test
    void validData() {
        var employees = ValidDataProvider.getEmployees().subList(0, 4);
        employeeRepository.saveAll(employees);

        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);

        var limits = ValidDataProvider.getTimeOffLimits(employees, types);
        timeOffTypeLimitPerYearAndEmployeeRepository.saveAll(limits);

        Assertions.assertDoesNotThrow(() -> timeOffRepository.saveAll(ValidDataProvider.getTimeOffs(limits)));
    }
}
