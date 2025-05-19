package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off.TimeOffTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class TimeOffEntityIntegrationTests {
    @Test
    void basicValidEntityPassesValidation() {
        assertEquals(0, ConstraintValidation.validate(TimeOffTestEntityFactory.build().asEntity()).size());
    }

    @Test
    void firstDayCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().firstDay(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("firstDay", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void lastDayInclusiveCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().lastDayInclusive(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("lastDayInclusive", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void hoursCountCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().hoursCount(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("hoursCount", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
    void hoursCountCanNotBeNegativeOrZero(int invalidHoursCount) {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().hoursCount(invalidHoursCount).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("hoursCount", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void commentCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().comment(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("comment", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void timeOffYearlyLimitCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().timeOffYearlyLimit(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("timeOffYearlyLimit", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void timeOffTypeCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().timeOffType(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("timeOffType", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void employeeCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().employee(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("employee", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void firstDayAfterLastDayTriggersCustomValidationAssertion() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().firstDay(LocalDate.of(2025, 12, 31)).lastDayInclusive(LocalDate.of(2025, 12, 30)).build().asEntity());
        assertFalse(validation.isEmpty());
        assertTrue(validation.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstDayAfterLastDay")));
    }

    @Test
    void monthNotEqualTriggersCustomValidationAssertion() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().firstDay(LocalDate.of(2025, 10, 10)).lastDayInclusive(LocalDate.of(2025, 11, 10)).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("yearAndMonthEqual", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void hoursCountMoreThanHoursInTimeOffTriggersCustomValidationAssertion() {
        var validation = ConstraintValidation.validate(TimeOffTestEntityFactory.builder().hoursCount(Integer.MAX_VALUE).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("hoursCountLessThanHoursInTimeOff", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void yearOutsideOfRangeTriggersCustomValidationAssertion() {
        var entity = TimeOffTestEntityFactory.build().asEntity();
        entity.setFirstDay(entity.getFirstDay().minusYears(3000));
        entity.setLastDayInclusive(entity.getFirstDay());
        var validation = ConstraintValidation.validate(entity);
        assertEquals(1, validation.size());
        assertEquals("yearInAcceptableRange", validation.iterator().next().getPropertyPath().toString());
    }
}
