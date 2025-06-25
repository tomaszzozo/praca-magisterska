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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: integration and unit
class EmployeeServiceUnitTest {

	private EmployeeRepository employeeRepository;
	private EmployeeService employeeService;

	@BeforeEach
	void setUp() {
		employeeRepository = mock(EmployeeRepository.class);
		employeeService = new EmployeeService(employeeRepository);
	}

	@Test
	void getAll_shouldReturnAllEmployees() {
		var emp1 = new EmployeeEntity();
		var emp2 = new EmployeeEntity();
		when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

		var result = employeeService.getAll();

		assertThat(result).containsExactly(emp1, emp2);
	}

	@Test
	void getById_shouldReturnEmployee_whenExists() {
		var emp = new EmployeeEntity();
		emp.setId(1L);
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

		var result = employeeService.getById(1L);

		assertThat(result).isEqualTo(emp);
	}

	@Test
	void getById_shouldThrow_whenEmployeeNotFound() {
		when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> employeeService.getById(2L))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("nie istnieje");
	}

	@Test
	void post_shouldSaveEmployee_whenEmailAndPhoneFree() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setFirstName("Anna");
		dto.setLastName("Kowalska");
		dto.setEmail("anna@example.com");
		dto.setPhoneNumber("111222333");
		dto.setAccessLevel(2);

		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(false);

		employeeService.post(dto);

		ArgumentCaptor<EmployeeEntity> captor = ArgumentCaptor.forClass(EmployeeEntity.class);
		verify(employeeRepository).save(captor.capture());
		EmployeeEntity saved = captor.getValue();

		assertThat(saved.getEmail()).isEqualTo("anna@example.com");
		assertThat(saved.getPhoneNumber()).isEqualTo("111222333");
		assertThat(saved.getAccessLevel()).isEqualTo(2);
	}

	@Test
	void post_shouldThrow_whenEmailExists() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("existing@example.com");
		dto.setPhoneNumber("123456789");

		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(true);

		assertThatThrownBy(() -> employeeService.post(dto))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("Email jest już zajęty");
	}

	@Test
	void post_shouldThrow_whenPhoneNumberExists() {
		EmployeePostDto dto = new EmployeePostDto();
		dto.setEmail("new@example.com");
		dto.setPhoneNumber("123456789");

		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(true);

		assertThatThrownBy(() -> employeeService.post(dto))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("Numer telefonu jest już zajęty");
	}

	@Test
	void put_shouldUpdateEmployee_whenDataIsValid() {
		EmployeeEntity existing = new EmployeeEntity();
		existing.setId(1L);
		existing.setEmail("old@example.com");
		existing.setPhoneNumber("111222333");

		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("new@example.com");
		dto.setPhoneNumber("444555666");
		dto.setFirstName("Updated");
		dto.setLastName("Employee");
		dto.setAccessLevel(3);

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
		when(employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())).thenReturn(false);

		employeeService.put(1L, dto);

		verify(employeeRepository).save(existing);
		assertThat(existing.getEmail()).isEqualTo("new@example.com");
		assertThat(existing.getPhoneNumber()).isEqualTo("444555666");
		assertThat(existing.getFirstName()).isEqualTo("Updated");
		// INACCURACY: does not check last name
		// INACCURACY: does not check access level
		// INACCURACY: does not check id
	}

	@Test
	void put_shouldThrow_whenEmailTakenByAnother() {
		EmployeeEntity existing = new EmployeeEntity();
		existing.setId(1L);
		existing.setEmail("original@example.com");
		existing.setPhoneNumber("999888777");

		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("taken@example.com");
		dto.setPhoneNumber("999888777");

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(employeeRepository.existsByEmail("taken@example.com")).thenReturn(true);

		assertThatThrownBy(() -> employeeService.put(1L, dto))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("Email jest już zajęty");
	}

	@Test
	void put_shouldThrow_whenPhoneNumberTakenByAnother() {
		EmployeeEntity existing = new EmployeeEntity();
		existing.setId(1L);
		existing.setEmail("test@example.com");
		existing.setPhoneNumber("999888777");

		EmployeePutDto dto = new EmployeePutDto();
		dto.setEmail("test@example.com");
		dto.setPhoneNumber("duplicated");

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(employeeRepository.existsByPhoneNumber("duplicated")).thenReturn(true);

		assertThatThrownBy(() -> employeeService.put(1L, dto))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("Numer telefonu jest już zajęty");
	}
}
