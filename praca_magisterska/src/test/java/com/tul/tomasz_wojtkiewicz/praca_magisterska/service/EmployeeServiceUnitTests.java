package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeePostDtoBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee.TestEmployeePutDtoBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceUnitTests {
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getAll() {
        var employee = new TestEmployeeEntityBuilder().build();
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(employee));
        var result = employeeService.getAll();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(employee, result.getFirst());
    }

    @Test
    void getById_employeeExists() {
        var employee = new TestEmployeeEntityBuilder().build();
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(employee));
        var result = employeeService.getById(1);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(employee, result);
        Mockito.verify(employeeRepository).findById(1L);
    }

    @Test
    void getById_employeeDoesNotExists() {
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> employeeService.getById(1)).getStatus());
        Mockito.verify(employeeRepository).findById(1L);
    }

    @Test
    void post_emailTaken() {
        var dto = new TestEmployeePostDtoBuilder().build();
        Mockito.when(employeeRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.post(dto)).getStatus());
        Mockito.verify(employeeRepository).existsByEmail(dto.getEmail());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(employeeRepository, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void post_phoneNumberTaken() {
        var dto = new TestEmployeePostDtoBuilder().build();
        Mockito.when(employeeRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(employeeRepository.existsByPhoneNumber(Mockito.anyString())).thenReturn(true);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.post(dto)).getStatus());
        Mockito.verify(employeeRepository).existsByEmail(dto.getEmail());
        Mockito.verify(employeeRepository).existsByPhoneNumber(dto.getPhoneNumber());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(employeeRepository, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void post_success() {
        var dto = new TestEmployeePostDtoBuilder().build();
        Mockito.when(employeeRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(employeeRepository.existsByPhoneNumber(Mockito.anyString())).thenReturn(false);
        Assertions.assertDoesNotThrow(() -> employeeService.post(dto));
        Mockito.verify(employeeRepository).save(new TestEmployeeEntityBuilder().build());
        Mockito.verify(employeeRepository).existsByEmail(dto.getEmail());
        Mockito.verify(employeeRepository).existsByPhoneNumber(dto.getPhoneNumber());
    }

    @Test
    void put_emailTaken() {
        var dto = new TestEmployeePutDtoBuilder().withEmail(TestEmployeeEntityBuilder.Defaults.email+".com").build();
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new TestEmployeeEntityBuilder().build()));
        Mockito.when(employeeRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.put(1, dto)).getStatus());
        Mockito.verify(employeeRepository).findById(1L);
        Mockito.verify(employeeRepository).existsByEmail(dto.getEmail());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(employeeRepository, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void put_phoneNumberTaken() {
        var dto = new TestEmployeePutDtoBuilder().withPhoneNumber(new StringBuilder(TestEmployeeEntityBuilder.Defaults.phoneNumber).reverse().toString()).build();
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new TestEmployeeEntityBuilder().build()));
        Mockito.when(employeeRepository.existsByPhoneNumber(Mockito.anyString())).thenReturn(true);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> employeeService.put(1, dto)).getStatus());
        Mockito.verify(employeeRepository).findById(1L);
        Mockito.verify(employeeRepository).existsByPhoneNumber(dto.getPhoneNumber());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(employeeRepository, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void put_success() {
        var dto = new TestEmployeePutDtoBuilder().withPhoneNumber(new StringBuilder(TestEmployeeEntityBuilder.Defaults.phoneNumber).reverse().toString()).withEmail(TestEmployeeEntityBuilder.Defaults.email+".com").build();
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new TestEmployeeEntityBuilder().build()));
        Mockito.when(employeeRepository.existsByPhoneNumber(Mockito.anyString())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Assertions.assertDoesNotThrow(() -> employeeService.put(1, dto));
        Mockito.verify(employeeRepository).findById(1L);
        Mockito.verify(employeeRepository).existsByPhoneNumber(dto.getPhoneNumber());
        Mockito.verify(employeeRepository).existsByEmail(dto.getEmail());
        Mockito.verify(employeeRepository).save(new TestEmployeeEntityBuilder().withEmail(dto.getEmail()).withPhoneNumber(dto.getPhoneNumber()).build());
    }
}
