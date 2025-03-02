package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

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

    private EmployeeEntity testEmployee;
    private TimeOffTypeEntity testType;
    private TimeOffTypeLimitPerYearAndEmployeeEntity testLimit;

    private static final LocalDate NOW = LocalDate.now();

    @BeforeEach
    void beforeEach() {
        testEmployee = DefaultTestEntities.getTestEmployee();
        employeeRepository.save(testEmployee);
        testType = DefaultTestEntities.getTestTimeOffType();
        timeOffTypeRepository.save(testType);
        testLimit = DefaultTestEntities.getTestTimeOffLimit(testType, testEmployee);
        timeOffTypeLimitPerYearAndEmployeeRepository.save(testLimit);
    }

    @AfterEach
    void afterEach() {
        timeOffRepository.deleteAll();
        timeOffTypeLimitPerYearAndEmployeeRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    private TimeOffEntity createTestTimeOff() {
        var timeOff = new TimeOffEntity();
        timeOff.setFirstDay(NOW);
        timeOff.setLastDayInclusive(NOW.plusDays(1));
        timeOff.setComment("");
        timeOff.setHoursCount(16);
        timeOff.setEmployee(testEmployee);
        timeOff.setTimeOffYearlyLimit(testLimit);
        timeOff.setTimeOffType(testType);
        return timeOff;
    }

    @Test
    void negativeHoursCountValidation() {
        List.of(Integer.MIN_VALUE, -1000, -1).forEach(h -> {
            var timeOff = createTestTimeOff();
            timeOff.setHoursCount(h);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        });
        var timeOff = createTestTimeOff();
        timeOff.setHoursCount(0);
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstDayAfterLastDayValidation() {
        List.of(1, 5, 10, 30).forEach(d -> {
            var timeOff = createTestTimeOff();
            timeOff.setLastDayInclusive(LocalDate.now());
            timeOff.setFirstDay(timeOff.getLastDayInclusive().plusDays(d));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        });
        var timeOff = createTestTimeOff();
        timeOff.setFirstDay(LocalDate.now());
        timeOff.setLastDayInclusive(timeOff.getFirstDay());
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void firstAndLastDayYearRangeValidation() {
        List.of(LocalDate.MIN.getYear(), -123456789, -12345678, -1234567, -123456, -12345, -1234, -123, -1, 0, 1, 123, 1234, 2000, 2019, 2101, 2200, 12345, 123456, 1234567, 12345678, 123456789, LocalDate.MAX.getYear()).forEach(y -> {
            var timeOff = createTestTimeOff();
            var timeOff2 = createTestTimeOff();
            timeOff.setFirstDay(LocalDate.of(y, 1, 1));
            timeOff2.setLastDayInclusive(LocalDate.of(y, 1, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        });
        {
            var timeOff = createTestTimeOff();
            timeOff.setFirstDay(LocalDate.of(2020, 1, 1));
            timeOff.setLastDayInclusive(timeOff.getFirstDay());
            Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
            timeOffRepository.deleteAll();
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setFirstDay(LocalDate.of(2100, 12, 31));
            timeOff.setLastDayInclusive(timeOff.getFirstDay());
            Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
            timeOffRepository.deleteAll();
        }

    }

    @Test
    void firstAndLastDayMonthAndYearMatchValidation() {
        for (int i = 2; i <= 12; i++) {
            var timeOff = createTestTimeOff();
            timeOff.setLastDayInclusive(LocalDate.of(2025, 1, 1));
            timeOff.setFirstDay(LocalDate.of(2025, i, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        for (int i = 2021; i < 2100; i++) {
            var timeOff = createTestTimeOff();
            timeOff.setFirstDay(LocalDate.of(2020, 1, 1));
            timeOff.setLastDayInclusive(LocalDate.of(i, 1, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
    }

    @Test
    void hoursCountNotMoreThanHoursInTimeOffPeriodValidation() {
        var timeOff = createTestTimeOff();
        timeOff.setHoursCount((int) timeOff.getFirstDay().until(timeOff.getLastDayInclusive().plusDays(1), ChronoUnit.DAYS) * 24 + 1);
        Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        timeOff.setHoursCount((int) timeOff.getFirstDay().until(timeOff.getLastDayInclusive().plusDays(1), ChronoUnit.DAYS) * 24);
        Assertions.assertDoesNotThrow(() -> timeOffRepository.save(timeOff));
    }

    @Test
    void notNullFieldsValidation() {
        {
            var timeOff = createTestTimeOff();
            timeOff.setComment(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setFirstDay(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setLastDayInclusive(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setTimeOffYearlyLimit(null);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setTimeOffType(null);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setEmployee(null);
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff));
        }
    }

    @Test
    void employeeFirstDayAndEmployeeLastDayUniqueness() {
        timeOffRepository.save(createTestTimeOff());
        {
            var timeOff = createTestTimeOff();
            timeOff.setLastDayInclusive(timeOff.getLastDayInclusive().plusDays(1));
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> timeOffRepository.save(timeOff));
        }
        {
            var timeOff = createTestTimeOff();
            timeOff.setFirstDay(timeOff.getFirstDay().plusDays(1));
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
