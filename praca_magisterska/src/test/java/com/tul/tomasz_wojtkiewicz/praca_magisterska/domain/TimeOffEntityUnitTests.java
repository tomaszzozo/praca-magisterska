package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

class TimeOffEntityUnitTests {
    private TimeOffEntity testTimeOff;

    private Set<ConstraintViolation<TimeOffEntity>> validateTimeOff() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(testTimeOff);
    }

    @BeforeEach
    void beforeEach() {
        testTimeOff = new TimeOffEntity();
        testTimeOff.setTimeOffYearlyLimit(new TimeOffLimitEntity());
        testTimeOff.setTimeOffType(new TimeOffTypeEntity());
        testTimeOff.setEmployee(new EmployeeEntity());
        testTimeOff.setComment("");
        testTimeOff.setFirstDay(LocalDate.of(2025, 3, 10));
        testTimeOff.setLastDayInclusive(testTimeOff.getFirstDay());
        testTimeOff.setHoursCount(8);
    }

    @Test
    void firstDayIsAfterLastDay() {
        testTimeOff = Mockito.spy(testTimeOff);
        Mockito.when(testTimeOff.isHoursCountLessThanHoursInTimeOff()).thenReturn(true);
        testTimeOff.setFirstDay(testTimeOff.getLastDayInclusive().plusDays(1));
        var validation = validateTimeOff();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("firstDayAfterLastDay", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void firstDayEqualsLastDay() {
        Assertions.assertTrue(validateTimeOff().isEmpty());
    }

    @Test
    void yearNotEqual() {
        testTimeOff.setFirstDay(LocalDate.of(2025, 1, 1));
        testTimeOff.setLastDayInclusive(testTimeOff.getFirstDay().plusYears(1));
        var validation = validateTimeOff();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("yearAndMonthEqual", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void monthNotEqual() {
        testTimeOff.setFirstDay(LocalDate.of(2025, 1, 1));
        testTimeOff.setLastDayInclusive(testTimeOff.getFirstDay().plusMonths(1));
        var validation = validateTimeOff();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("yearAndMonthEqual", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void yearAndMonthNotEqual() {
        testTimeOff.setFirstDay(LocalDate.of(2025, 1, 1));
        testTimeOff.setLastDayInclusive(testTimeOff.getFirstDay().plusYears(1).plusMonths(1));
        var validation = validateTimeOff();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("yearAndMonthEqual", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void hoursMoreThanHoursInTimeOffRange() {
        testTimeOff.setHoursCount((int) testTimeOff.getFirstDay().until(testTimeOff.getLastDayInclusive(), ChronoUnit.DAYS) * 24 + 24 + 1);
        var validation = validateTimeOff();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("hoursCountLessThanHoursInTimeOff", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void hoursEqualHoursInTimeOffRange() {
        testTimeOff.setHoursCount((int) testTimeOff.getFirstDay().until(testTimeOff.getLastDayInclusive(), ChronoUnit.DAYS) * 24 + 24);
        Assertions.assertTrue(validateTimeOff().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
    void firstDayYearOutsideOfRange(int invalidYear) {
        testTimeOff.setFirstDay(LocalDate.of(invalidYear, 1, 1));
        testTimeOff.setLastDayInclusive(LocalDate.of(2025, 1, 1));
        var validation = validateTimeOff();
        Assertions.assertFalse(validation.isEmpty());
        Assertions.assertTrue(validation.stream().anyMatch(o -> o.getPropertyPath().toString().equals("yearInAcceptableRange")));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
    void lastDayYearOutsideOfRange(int invalidYear) {
        testTimeOff.setFirstDay(LocalDate.of(2025, 1, 1));
        testTimeOff.setLastDayInclusive(LocalDate.of(invalidYear, 1, 1));
        var validation = validateTimeOff();
        Assertions.assertFalse(validation.isEmpty());
        Assertions.assertTrue(validation.stream().anyMatch(o -> o.getPropertyPath().toString().equals("yearInAcceptableRange")));
    }
}
