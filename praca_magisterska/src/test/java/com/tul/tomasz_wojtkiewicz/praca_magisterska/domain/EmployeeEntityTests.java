package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestEntities;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@SpringBootTest
class EmployeeEntityTests {
    @Autowired
    private EmployeeRepository employeeRepository;

    @AfterEach
    void afterEach() {
        employeeRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#provideInvalidEmails")
    void emailValidation(String invalidEmail) {
        var employee = DefaultTestEntities.getTestEmployee();
        employee.setEmail(invalidEmail);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#provideInvalidNames")
    void firstAndLastNameValidation(String invalidName) {
        var employee1 = DefaultTestEntities.getTestEmployee();
        employee1.setFirstName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee1));
        var employee2 = DefaultTestEntities.getTestEmployee();
        employee2.setLastName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee2));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#provideInvalidPhoneNumbers")
    void phoneNumberValidation(String invalidPhoneNumber) {
        var employee = DefaultTestEntities.getTestEmployee();
        employee.setPhoneNumber(invalidPhoneNumber);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
    }

    @Test
    void accessLevelValidation() {
        List.of(Integer.MIN_VALUE, -1234567890, -123456789, -12345678, -1234567, -123456, -12345, -1234, -123, -12, -1, 4, 12, 123, 1234, 12345, 123456, 1234567, 12345678, 123456789, 1234567890, Integer.MAX_VALUE).forEach(i -> {
            var employee = DefaultTestEntities.getTestEmployee();
            employee.setAccessLevel(i);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        });
        for (int lvl = 0; lvl <= 3; lvl++) {
            var employee = DefaultTestEntities.getTestEmployee();
            employee.setAccessLevel(lvl);
            Assertions.assertDoesNotThrow(() -> employeeRepository.save(employee));
            employeeRepository.deleteAll();
        }
    }

    @Test
    void notNullFieldsValidation() {
        {
            var employee = DefaultTestEntities.getTestEmployee();
            employee.setEmail(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = DefaultTestEntities.getTestEmployee();
            employee.setFirstName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = DefaultTestEntities.getTestEmployee();
            employee.setLastName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = DefaultTestEntities.getTestEmployee();
            employee.setPhoneNumber(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
    }

    @Test
    void validData() {
        ValidDataProvider.getEmployees().forEach(e -> Assertions.assertDoesNotThrow(() -> employeeRepository.save(e)));
    }

    @Test
    void emailUniqueness() {
        var defaultEmployee = DefaultTestEntities.getTestEmployee();
        employeeRepository.save(defaultEmployee);
        var employee = DefaultTestEntities.getTestEmployee();
        employee.setPhoneNumber(new StringBuilder(defaultEmployee.getPhoneNumber()).reverse().toString());
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(employee));
    }

    @Test
    void phoneNumberUniqueness() {
        var defaultEmployee = DefaultTestEntities.getTestEmployee();
        employeeRepository.save(defaultEmployee);
        var employee = DefaultTestEntities.getTestEmployee();
        employee.setEmail(defaultEmployee.getEmail().replaceAll("@", "1@"));
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(employee));
    }
}
