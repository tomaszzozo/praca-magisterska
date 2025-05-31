package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ConstraintValidation;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name.Name;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumber;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@Tag("entity")
class EmployeeEntityIntegrationTests {
    @Test
    void basicValidEntityPassesValidation() {
        assertEquals(0, ConstraintValidation.validate(EmployeeTestEntityFactory.build().asEntity()).size());
    }

    @Test
    void nameValidatorIsUsedInFirstNameValidation() {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().firstName("3").build().asEntity());
        assertEquals(1, validation.size());
        assertEquals(Name.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void firstNameCanNotBeNull() {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().firstName(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("firstName", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void nameValidatorIsUsedInLastNameValidation() {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().lastName("3").build().asEntity());
        assertEquals(1, validation.size());
        assertEquals(Name.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void lastNameCanNotBeNull() {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().lastName(null).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("lastName", validation.iterator().next().getPropertyPath().toString());
    }

    @Test
    void phoneNumberValidatorIsUsedInPhoneNumberValidation() {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().phoneNumber("3").build().asEntity());
        assertEquals(1, validation.size());
        assertEquals(PhoneNumber.class, validation.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#emails")
    void entityWithInvalidEmailDoesNotPassTheValidation(String invalidEmail) {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().email(invalidEmail).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("email", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#accessLevels")
    void entityWithInvalidAccessLevelsDoesNotPassTheValidation(Integer invalidAccessLevel) {
        var validation = ConstraintValidation.validate(EmployeeTestEntityFactory.builder().accessLevel(invalidAccessLevel).build().asEntity());
        assertEquals(1, validation.size());
        assertEquals("accessLevel", validation.iterator().next().getPropertyPath().toString());
    }
}
