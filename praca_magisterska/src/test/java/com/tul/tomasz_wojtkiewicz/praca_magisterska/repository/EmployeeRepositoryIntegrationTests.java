package com.tul.tomasz_wojtkiewicz.praca_magisterska.repository;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.function.Consumer;
import java.util.stream.Stream;

@DataJpaTest
class EmployeeRepositoryIntegrationTests {
    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeEntity testEmployee;

    private static Stream<Arguments> nullSetters() {
        return Stream.of(
                Arguments.of("First name", (Consumer<EmployeeEntity>) (e -> e.setFirstName(null))),
                Arguments.of("Last name", (Consumer<EmployeeEntity>) (e -> e.setLastName(null))),
                Arguments.of("Email", (Consumer<EmployeeEntity>) (e -> e.setEmail(null))),
                Arguments.of("Phone number", (Consumer<EmployeeEntity>) (e -> e.setPhoneNumber(null)))
        );
    }

    @BeforeEach
    void beforeEach() {
        testEmployee = DefaultTestObjects.getEmployeeEntity();
    }

    @Test
    void validData() {
        Assertions.assertDoesNotThrow(() -> employeeRepository.saveAll(ValidDataProvider.getEmployees()));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#emails")
    void invalidEmail(String invalidEmail) {
        testEmployee.setEmail(invalidEmail);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#names")
    void invalidFirstName(String invalidName) {
        testEmployee.setFirstName(invalidName);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#names")
    void invalidLastName(String invalidName) {
        testEmployee.setLastName(invalidName);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#phoneNumbers")
    void invalidPhoneNumber(String invalidPhoneNumber) {
        testEmployee.setPhoneNumber(invalidPhoneNumber);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#accessLevels")
    void invalidAccessLevel(int invalidAccessLevel) {
        testEmployee.setAccessLevel(invalidAccessLevel);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @ParameterizedTest
    @MethodSource("nullSetters")
    void nullFields(String ignoredFieldName, Consumer<EmployeeEntity> nullSetter) {
        nullSetter.accept(testEmployee);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @Test
    void emailNotUnique() {
        employeeRepository.save(testEmployee);
        testEmployee = DefaultTestObjects.getEmployeeEntity();
        testEmployee.setPhoneNumber(new StringBuilder(testEmployee.getPhoneNumber()).reverse().toString());
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @Test
    void phoneNumberNotUnique() {
        employeeRepository.save(testEmployee);
        testEmployee = DefaultTestObjects.getEmployeeEntity();
        testEmployee.setEmail(testEmployee.getEmail() + ".com");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(testEmployee));
    }

    @Test
    void emailAndPhoneNumberNotUnique() {
        employeeRepository.save(testEmployee);
        testEmployee = DefaultTestObjects.getEmployeeEntity();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(testEmployee));
    }
}
