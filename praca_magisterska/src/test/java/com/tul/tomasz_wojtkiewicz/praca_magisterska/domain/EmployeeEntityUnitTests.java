package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;

class EmployeeEntityUnitTests {
    private EmployeeEntity testEmployee;

    private Set<ConstraintViolation<EmployeeEntity>> validateTestEmployee() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(testEmployee);
    }

    @BeforeEach
    void beforeEach() {
        testEmployee = DefaultTestObjects.getEmployeeEntity();
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#names")
    void invalidFirstName(String invalidName) {
        testEmployee.setFirstName(invalidName);
        var validation = validateTestEmployee();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("firstName", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#names")
    void invalidLastName(String invalidName) {
        testEmployee.setLastName(invalidName);
        var validation = validateTestEmployee();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("lastName", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#phoneNumbers")
    void invalidPhoneNumber(String invalidPhoneNumber) {
        testEmployee.setPhoneNumber(invalidPhoneNumber);
        var validation = validateTestEmployee();
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("phoneNumber", validation.iterator().next().getPropertyPath().toString());
    }
}
