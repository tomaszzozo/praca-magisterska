// Testy jednostkowe EmployeeService
package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito; // INACCURACY: unused import
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class EmployeeServiceUnitTest {

	private EmployeeRepository employeeRepository;
	private EmployeeService employeeService;

	@BeforeEach
	void setUp() {
		employeeRepository = mock(EmployeeRepository.class);
		employeeService = new EmployeeService(employeeRepository);
	}

	@Test
		// cases: 1
	void getAll_ShouldReturnAllEmployees() {
		List<EmployeeEntity> mockList = List.of(new EmployeeEntity(), new EmployeeEntity());
		when(employeeRepository.findAll()).thenReturn(mockList);

		List<EmployeeEntity> result = employeeService.getAll();

		assertEquals(mockList, result);
		verify(employeeRepository).findAll();
	}

	@Test
		// cases: 1
	void getById_ShouldReturnEmployee_WhenExists() {
		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(1L);
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		EmployeeEntity result = employeeService.getById(1L);

		assertEquals(employee, result);
	}

	@Test
		// cases: 1
	void getById_ShouldThrowApiException_WhenNotFound() {
		when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> employeeService.getById(1L));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
		assertEquals("Pracownik o podanym id nie istnieje", ex.getMessage());
	}

	@Test
		// cases: 1
	void post_ShouldSaveEmployee_WhenValidAndNoConflicts() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("email@example.com");
		dto.setPhoneNumber("123456789");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setAccessLevel(1);

		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(false);

		employeeService.post(dto);

		ArgumentCaptor<EmployeeEntity> captor = ArgumentCaptor.forClass(EmployeeEntity.class);
		verify(employeeRepository).save(captor.capture());
		EmployeeEntity saved = captor.getValue();
		assertEquals(dto.getEmail(), saved.getEmail());
		assertEquals(dto.getPhoneNumber(), saved.getPhoneNumber());
		assertEquals(dto.getFirstName(), saved.getFirstName());
		assertEquals(dto.getLastName(), saved.getLastName());
		assertEquals(dto.getAccessLevel(), saved.getAccessLevel());
	}

	@Test
		// cases: 1
	void post_ShouldThrowApiException_WhenEmailExists() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("email@example.com");
		dto.setPhoneNumber("123456789");

		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> employeeService.post(dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Email jest już zajęty", ex.getMessage());
		verify(employeeRepository, never()).save(any());
	}

	@Test
		// cases: 1
	void post_ShouldThrowApiException_WhenPhoneNumberExists() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("email@example.com");
		dto.setPhoneNumber("123456789");

		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> employeeService.post(dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Numer telefonu jest już zajęty", ex.getMessage());
		verify(employeeRepository, never()).save(any());
	}

	@Test
		// cases: 1
	void put_ShouldUpdateEmployee_WhenValidAndNoConflicts() {
		long id = 1L;
		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("newemail@example.com");
		dto.setPhoneNumber("987654321");
		dto.setFirstName("Jane");
		dto.setLastName("Smith");
		dto.setAccessLevel(2);

		EmployeeEntity original = new EmployeeEntity();
		original.setId(id);
		original.setEmail("oldemail@example.com");
		original.setPhoneNumber("123456789");
		original.setFirstName("Old");
		original.setLastName("Name");
		original.setAccessLevel(1);

		when(employeeRepository.findById(id)).thenReturn(Optional.of(original));
		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(false);

		employeeService.put(id, dto);

		ArgumentCaptor<EmployeeEntity> captor = ArgumentCaptor.forClass(EmployeeEntity.class);
		verify(employeeRepository).save(captor.capture());
		EmployeeEntity saved = captor.getValue();

		assertEquals(dto.getEmail(), saved.getEmail());
		assertEquals(dto.getPhoneNumber(), saved.getPhoneNumber());
		assertEquals(dto.getFirstName(), saved.getFirstName());
		assertEquals(dto.getLastName(), saved.getLastName());
		assertEquals(dto.getAccessLevel(), saved.getAccessLevel());
	}

	@Test
		// cases: 1
	void put_ShouldThrowApiException_WhenEmployeeNotFound() {
		long id = 1L;
		EmployeePutDto dto = new EmployeePutDto();
		when(employeeRepository.findById(id)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> employeeService.put(id, dto));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
		assertEquals("Pracownik o podanym id nie istnieje", ex.getMessage());
	}

	@Test
		// cases: 1
	void put_ShouldThrowApiException_WhenEmailExistsOnAnotherEmployee() {
		long id = 1L;
		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("conflict@example.com");
		dto.setPhoneNumber("123456789");

		EmployeeEntity original = new EmployeeEntity();
		original.setId(id);
		original.setEmail("oldemail@example.com");
		original.setPhoneNumber("123456789");

		when(employeeRepository.findById(id)).thenReturn(Optional.of(original));
		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> employeeService.put(id, dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Email jest już zajęty", ex.getMessage());
		verify(employeeRepository, never()).save(any());
	}

	@Test
		// cases: 1
	void put_ShouldThrowApiException_WhenPhoneNumberExistsOnAnotherEmployee() {
		long id = 1L;
		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("email@example.com");
		dto.setPhoneNumber("conflictnumber");

		EmployeeEntity original = new EmployeeEntity();
		original.setId(id);
		original.setEmail("email@example.com");
		original.setPhoneNumber("oldnumber");

		when(employeeRepository.findById(id)).thenReturn(Optional.of(original));
		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> employeeService.put(id, dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Numer telefonu jest już zajęty", ex.getMessage());
		verify(employeeRepository, never()).save(any());
	}
}
