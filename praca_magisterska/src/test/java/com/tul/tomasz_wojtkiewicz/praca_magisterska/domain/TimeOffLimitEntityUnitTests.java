package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TimeOffLimitEntityUnitTests {
    private TimeOffLimitEntity testLimit;

    private Set<ConstraintViolation<TimeOffLimitEntity>> validateLimit() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(testLimit);
    }

    @BeforeEach
    void beforeEach() {
        testLimit = DefaultTestObjects.getLimitEntity(new TimeOffTypeEntity(), new EmployeeEntity());
    }

    @ParameterizedTest
    @MethodSource("years")
    void maxHoursMoreThanHoursInYear(int year) {
        testLimit.setLeaveYear(year);
        testLimit.setMaxHours(LocalDate.of(year, 12, 31).getDayOfYear()*24+1);
        var validation = validateLimit();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("maxHoursNotHigherThanHoursInYear", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("years")
    void maxHoursEqualsHoursInYear(int year) {
        testLimit.setLeaveYear(year);
        testLimit.setMaxHours(LocalDate.of(year, 12, 31).getDayOfYear()*24);
        Assertions.assertTrue(validateLimit().isEmpty());
    }

    private static Stream<Arguments> years() {
        return IntStream.range(2024, 2028).mapToObj(Arguments::of);
    }
}
