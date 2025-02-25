package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmployeeEntityTests {
    @Autowired
    private EmployeeRepository employeeRepository;


    private static EmployeeEntity createTestEmployee() {
        var employee = new EmployeeEntity();
        employee.setAccessLevel(0);
        employee.setFirstName("Tester");
        employee.setLastName("Tester");
        employee.setPhoneNumber("123456789");
        employee.setEmail("tester@test.com");
        return employee;
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.InvalidDataProvider#provideInvalidEmails")
    void when_iSaveEmployeeWithInvalidEmail_then_constraintViolationExceptionIsThrown(String invalidEmail) {
        var employee = createTestEmployee();
        employee.setEmail(invalidEmail);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.InvalidDataProvider#provideInvalidNames")
    void when_iSaveEmployeeWithInvalidFirstNameOrInvalidLastName_then_constraintViolationExceptionIsThrown(String invalidName) {
        var employee1 = createTestEmployee();
        employee1.setFirstName(invalidName);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee1));
        var employee2 = createTestEmployee();
        employee2.setLastName(invalidName);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee2));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.InvalidDataProvider#provideInvalidPhoneNumbers")
    void when_iSaveEmployeeWithInvalidPhoneNumber_then_constraintViolationExceptionIsThrown(String invalidPhoneNumber) {
        var employee = createTestEmployee();
        employee.setPhoneNumber(invalidPhoneNumber);
        Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
    }

    @Test
    void when_iSaveEmployeeWithInvalidAccessLevel_then_constraintViolationExceptionIsThrown() {
        for (int i = Integer.MIN_VALUE; i < 0; i += 7483648) {
            var employee = createTestEmployee();
            employee.setAccessLevel(i);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = createTestEmployee();
            employee.setAccessLevel(-1);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
        for (int i = 4; i > 0; i += 7483648) {
            var employee = createTestEmployee();
            employee.setAccessLevel(i);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
    }

    @Test
    void when_iSaveEmployeeWithNullInOneOfStrings_then_constraintViolationExceptionIsThrown() {
        {
            var employee = createTestEmployee();
            employee.setEmail(null);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = createTestEmployee();
            employee.setFirstName(null);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = createTestEmployee();
            employee.setLastName(null);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
        {
            var employee = createTestEmployee();
            employee.setPhoneNumber(null);
            Assertions.assertThrows(ConstraintViolationException.class, () -> employeeRepository.save(employee));
        }
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.ValidDataProvider#provideValidEmployeeCreationData")
    void when_iSaveEmployeeWithValidData_then_noExceptionIsThrown(String firstName, String lastName, String email, String phoneNumber) {
        var employee = new EmployeeEntity();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumber(phoneNumber);
        Assertions.assertDoesNotThrow(() -> employeeRepository.save(employee));
    }
}
