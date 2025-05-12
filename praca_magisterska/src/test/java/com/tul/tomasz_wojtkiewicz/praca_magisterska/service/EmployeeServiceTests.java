package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;


@SpringBootTest
class EmployeeServiceTests {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;

    @AfterEach
    void afterEach() {
        employeeRepository.deleteAll();
    }

    @Test
    void getById() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        Assertions.assertEquals(employee, employeeService.getById(employee.getId()));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> employeeService.getById(employee.getId() + 1L)).getStatus());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> employeeService.getById(employee.getId() - 1L)).getStatus());
    }

    @Test
    void getByIdOutOfRange() {
        List.of(Long.MIN_VALUE, -54321L, -1L, 0L).forEach(id -> Assertions.assertThrows(ValidationException.class, () -> employeeService.getById(id)));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
    void postInvalidNames(String invalidName) {
        var dto = DefaultTestObjects.getEmployeePostDto();
        dto.setFirstName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        dto.setFirstName("Name");
        dto.setLastName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#emails")
    void postInvalidEmail(String invalidEmail) {
        var dto = DefaultTestObjects.getEmployeePostDto();
        dto.setEmail(invalidEmail);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#phoneNumbers")
    void postInvalidPhoneNumbers(String invalidPhoneNumber) {
        var dto = DefaultTestObjects.getEmployeePostDto();
        dto.setPhoneNumber(invalidPhoneNumber);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#accessLevels")
    void postInvalidAccessLevel(int invalidAccessLevel) {
        var dto = DefaultTestObjects.getEmployeePostDto();
        dto.setAccessLevel(invalidAccessLevel);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
    }

    @Test
    void postNullFields() {
        {
            var dto = DefaultTestObjects.getEmployeePostDto();
            dto.setFirstName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        }
        {
            var dto = DefaultTestObjects.getEmployeePostDto();
            dto.setLastName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        }
        {
            var dto = DefaultTestObjects.getEmployeePostDto();
            dto.setEmail(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        }
        var dto = DefaultTestObjects.getEmployeePostDto();
        dto.setPhoneNumber(null);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
    }

    @Test
    void postValidDataCreatesNewEntry() {
        var dto = DefaultTestObjects.getEmployeePostDto();
        Assertions.assertDoesNotThrow(() -> employeeService.post(dto));
        Assertions.assertEquals(1, employeeRepository.count());
        var result = employeeRepository.findAll().getFirst();
        Assertions.assertEquals(dto.getEmail(), result.getEmail());
        Assertions.assertEquals(dto.getAccessLevel(), result.getAccessLevel());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void postEmailOrPhoneNumberAlreadyExists() {
        var dto = DefaultTestObjects.getEmployeePostDto();
        employeeService.post(dto);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.post(dto)).getStatus());

        dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.post(dto)).getStatus());

        dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
        dto.setEmail(dto.getEmail().replace("@", "1@"));
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.post(dto)).getStatus());

        dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
        Assertions.assertDoesNotThrow(() -> employeeService.post(dto));
    }

    @Test
    void putIdOutOfRange() {
        List.of(Long.MIN_VALUE, -54321L, -1L, 0L).forEach(id -> Assertions.assertThrows(ValidationException.class, () -> employeeService.put(id, DefaultTestObjects.getEmployeePutDto())));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
    void putInvalidNames(String invalidName) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var dto = DefaultTestObjects.getEmployeePutDto();
        dto.setFirstName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.put(employee.getId(), dto));
        dto.setFirstName("Name");
        dto.setLastName(invalidName);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.put(employee.getId(), dto));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#emails")
    void putInvalidEmail(String invalidEmail) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var dto = DefaultTestObjects.getEmployeePutDto();
        dto.setEmail(invalidEmail);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.put(employee.getId(), dto));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#phoneNumbers")
    void putInvalidPhoneNumbers(String invalidPhoneNumber) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var dto = DefaultTestObjects.getEmployeePutDto();
        dto.setPhoneNumber(invalidPhoneNumber);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.put(employee.getId(), dto));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#accessLevels")
    void putInvalidAccessLevel(int invalidAccessLevel) {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var dto = DefaultTestObjects.getEmployeePutDto();
        dto.setAccessLevel(invalidAccessLevel);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.put(employee.getId(), dto));
    }

    @Test
    void putNullFields() {
        {
            var dto = DefaultTestObjects.getEmployeePutDto();
            dto.setFirstName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        }
        {
            var dto = DefaultTestObjects.getEmployeePutDto();
            dto.setLastName(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        }
        {
            var dto = DefaultTestObjects.getEmployeePutDto();
            dto.setEmail(null);
            Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
        }
        var dto = DefaultTestObjects.getEmployeePutDto();
        dto.setPhoneNumber(null);
        Assertions.assertThrows(ValidationException.class, () -> employeeService.post(dto));
    }

    @Test
    void putValidDataUpdatesEntry() {
        var dto = DefaultTestObjects.getEmployeePutDto();
        employeeService.post(dto);
        var id = employeeRepository.findAll().getFirst().getId();

        dto.setEmail(dto.getEmail().replace("@", "1@"));
        dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
        dto.setAccessLevel(dto.getAccessLevel()+1 > 3 ? 0 : dto.getAccessLevel()+1);
        Assertions.assertDoesNotThrow(() -> employeeService.put(id, dto));
        Assertions.assertEquals(1, employeeRepository.count());

        var result = employeeRepository.findAll().getFirst();
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(dto.getEmail(), result.getEmail());
        Assertions.assertEquals(dto.getAccessLevel(), result.getAccessLevel());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void putEmailOrPhoneNumberAlreadyExists() {
        {
            var dto = DefaultTestObjects.getEmployeePutDto();
            employeeService.post(dto);
            dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
            dto.setEmail(dto.getEmail().replace("@", "@1"));
            employeeService.post(dto);
        }
        var id = employeeRepository.findAll().getLast().getId();
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.put(id, DefaultTestObjects.getEmployeePutDto())).getStatus());
        {
            var dto = DefaultTestObjects.getEmployeePutDto();
            dto.setEmail(dto.getEmail().replace("@", "@1"));
            Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.put(id, dto)).getStatus());
        }
        {
            var dto = DefaultTestObjects.getEmployeePutDto();
            dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
            Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.put(id, dto)).getStatus());
        }
        var dto = DefaultTestObjects.getEmployeePutDto();
        dto.setEmail(dto.getEmail().replace("@", "@1"));
        dto.setPhoneNumber(new StringBuilder(dto.getPhoneNumber()).reverse().toString());
        Assertions.assertDoesNotThrow(() -> employeeService.put(id, dto));
    }
}
