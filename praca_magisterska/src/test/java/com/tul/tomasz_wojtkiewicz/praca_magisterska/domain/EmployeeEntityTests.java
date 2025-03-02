package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
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

@SpringBootTest
class EmployeeEntityTests {
    @Autowired
    private EmployeeRepository employeeRepository;

    @AfterEach
    void afterEach() {
        employeeRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#emails")
    void emailValidation(String invalidEmail) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employee.setEmail(invalidEmail);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#names")
    void firstAndLastNameValidation(String invalidName) {
        var employee1 = DefaultTestObjects.getEmployeeEntity();
        employee1.setFirstName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee1));
        var employee2 = DefaultTestObjects.getEmployeeEntity();
        employee2.setLastName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee2));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#phoneNumbers")
    void phoneNumberValidation(String invalidPhoneNumber) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employee.setPhoneNumber(invalidPhoneNumber);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.InvalidDataProvider#accessLevels")
    void accessLevelInvalid(int invalidAccessLevel) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employee.setAccessLevel(invalidAccessLevel);
        Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
    }

    @Test
    void accessLevelValid() {
        for (int lvl = 0; lvl <= 3; lvl++) {
            var employee = DefaultTestObjects.getEmployeeEntity();
            employee.setAccessLevel(lvl);
            Assertions.assertDoesNotThrow(() -> employeeRepository.save(employee));
            employeeRepository.deleteAll();
        }
    }

    @Test
    void notNullFieldsValidation() {
        {
            var employee = DefaultTestObjects.getEmployeeEntity();
            employee.setEmail(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = DefaultTestObjects.getEmployeeEntity();
            employee.setFirstName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = DefaultTestObjects.getEmployeeEntity();
            employee.setLastName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = DefaultTestObjects.getEmployeeEntity();
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
        var defaultEmployee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(defaultEmployee);
        var employee = DefaultTestObjects.getEmployeeEntity();
        employee.setPhoneNumber(new StringBuilder(defaultEmployee.getPhoneNumber()).reverse().toString());
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(employee));
    }

    @Test
    void phoneNumberUniqueness() {
        var defaultEmployee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(defaultEmployee);
        var employee = DefaultTestObjects.getEmployeeEntity();
        employee.setEmail(defaultEmployee.getEmail().replaceAll("@", "1@"));
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.save(employee));
    }
}
