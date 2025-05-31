package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("entity")
class TimeOffLimitEntityIntegrationTests {
    @Test
    void basicValidEntityPassesValidation() {
        assertEquals(0, ConstraintValidation.validate(TimeOffLimitTestEntityFactory.build().asEntity()).size());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#hoursInYearNegative")
    void maxHoursCanNotBeNegative(int invalidMaxHours) {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().maxHours(invalidMaxHours).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("maxHours", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void maxHoursCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().maxHours(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("maxHours", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void defaultMaxHoursIsZero() {
        assertEquals(0, new TimeOffLimitEntity().getMaxHours());
    }

    @Test
    void maxHoursMoreThanHoursInYearTriggersCustomValidationAssertion() {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().leaveYear(2025).maxHours(LocalDate.of(2025, 12, 31).getDayOfYear()*24+1).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("maxHoursNotHigherThanHoursInYear", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void leaveYearCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().leaveYear(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("leaveYear", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
    void leaveYearMustBeInRange(int invalidLeaveYear) {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().leaveYear(invalidLeaveYear).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("leaveYear", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void employeeMustNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().employee(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("employee", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void timeOffTypeMustNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffLimitTestEntityFactory.builder().timeOffType(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("timeOffType", validation.iterator().next().getPropertyPath().toString());
    }
}
