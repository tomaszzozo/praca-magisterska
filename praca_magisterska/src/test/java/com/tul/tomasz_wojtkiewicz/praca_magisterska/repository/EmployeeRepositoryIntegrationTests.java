package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.stream.Stream;

@DataJpaTest
class EmployeeRepositoryIntegrationTests {
    @Autowired
    private EmployeeRepository employeeRepository;

    private static Stream<Arguments> nullableEmployees() {
        return Stream.of(
                Arguments.of("First name", new TestEmployeeEntityBuilder().withFirstName(null).build()),
                Arguments.of("Last name", new TestEmployeeEntityBuilder().withLastName(null).build()),
                Arguments.of("Email", new TestEmployeeEntityBuilder().withEmail(null).build()),
                Arguments.of("Phone number", new TestEmployeeEntityBuilder().withPhoneNumber(null).build())
        );
    }

    @Test
    void validData() {
        Assertions.assertDoesNotThrow(() -> employeeRepository.saveAll(ValidDataProvider.getEmployees()));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#emails")
    void invalidEmail(String invalidEmail) {
        var testEmployee = new TestEmployeeEntityBuilder().withEmail(invalidEmail).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
    void invalidFirstName(String invalidName) {
        var testEmployee = new TestEmployeeEntityBuilder().withFirstName(invalidName).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
    void invalidLastName(String invalidName) {
        var testEmployee = new TestEmployeeEntityBuilder().withLastName(invalidName).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#phoneNumbers")
    void invalidPhoneNumber(String invalidPhoneNumber) {
        var testEmployee = new TestEmployeeEntityBuilder().withPhoneNumber(invalidPhoneNumber).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#accessLevels")
    void invalidAccessLevel(int invalidAccessLevel) {
        var testEmployee = new TestEmployeeEntityBuilder().withAccessLevel(invalidAccessLevel).build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("nullableEmployees")
    void nullFields(String ignoredFieldName, EmployeeEntity nullableEmployee) {
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(nullableEmployee));
    }

    @Test
    void emailNotUnique() {
        employeeRepository.save(new TestEmployeeEntityBuilder().build());
        var testEmployee = new TestEmployeeEntityBuilder().withPhoneNumber(new StringBuilder(TestEmployeeEntityBuilder.Defaults.phoneNumber).reverse().toString()).build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @Test
    void phoneNumberNotUnique() {
        employeeRepository.save(new TestEmployeeEntityBuilder().build());
        var testEmployee = new TestEmployeeEntityBuilder().withEmail(TestEmployeeEntityBuilder.Defaults.email + ".com").build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @Test
    void emailAndPhoneNumberNotUnique() {
        employeeRepository.save(new TestEmployeeEntityBuilder().build());
        var testEmployee = new TestEmployeeEntityBuilder().build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(testEmployee));
    }
}
