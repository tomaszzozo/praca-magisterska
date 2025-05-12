package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeeEntityBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;

class EmployeeEntityUnitTests {
    private static Set<ConstraintViolation<EmployeeEntity>> validateTestEmployee(EmployeeEntity employee) {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(employee);
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
    void invalidFirstName(String invalidName) {
        var validation = validateTestEmployee(new TestEmployeeEntityBuilder().withFirstName(invalidName).build());
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("firstName", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
    void invalidLastName(String invalidName) {
        var validation = validateTestEmployee(new TestEmployeeEntityBuilder().withLastName(invalidName).build());
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("lastName", validation.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#phoneNumbers")
    void invalidPhoneNumber(String invalidPhoneNumber) {
        var validation = validateTestEmployee(new TestEmployeeEntityBuilder().withPhoneNumber(invalidPhoneNumber).build());
        Assertions.assertEquals(1, validation.size());
        Assertions.assertEquals("phoneNumber", validation.iterator().next().getPropertyPath().toString());
    }
}
