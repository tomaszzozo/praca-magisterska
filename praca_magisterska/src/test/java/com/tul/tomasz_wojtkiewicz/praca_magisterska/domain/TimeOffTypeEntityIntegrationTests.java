package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
class TimeOffTypeEntityIntegrationTests {
    @Test
    void basicValidEntityPassesValidation() {
        assertEquals(0, ConstraintValidation.validate(TimeOffTypeTestEntityFactory.build().asEntity()).size());
    }

    @Test
    void nameCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTypeTestEntityFactory.builder().name(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("name", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void nameCanNotBeBlank() {
        var validation = ConstraintValidation.validate(TimeOffTypeTestEntityFactory.builder().name("").build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("name", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void compensationPercentageCanNotBeNull() {
        var validation = ConstraintValidation.validate(TimeOffTypeTestEntityFactory.builder().compensationPercentage(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("compensationPercentage", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#compensationPercentages")
    void entityWithInvalidCompensationPercentageDoesNotPassTheValidation(Float invalidCompensationPercentage) {
        var validation = ConstraintValidation.validate(TimeOffTypeTestEntityFactory.builder().compensationPercentage(invalidCompensationPercentage).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("compensationPercentage", validation.iterator().next().getPropertyPath().toString());
    }
}
