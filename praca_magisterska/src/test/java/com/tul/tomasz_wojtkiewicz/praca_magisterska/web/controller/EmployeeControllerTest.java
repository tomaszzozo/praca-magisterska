package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;// Testy jednostkowe dla EmployeeController

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.EmployeeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.EmployeeGetDto; // INACCURACY: unused import
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor; // INACCURACY: unused import
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class EmployeeControllerTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller'

	private EmployeeService employeeService;
	private EmployeeController employeeController;

	@BeforeEach
	void setUp() {
		employeeService = mock(EmployeeService.class);
		employeeController = new EmployeeController(employeeService);
	}

	@Test
	void getAll_shouldReturnListOfEmployeeGetDto() {
		var entity1 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity.class);
		var entity2 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity.class);
		when(employeeService.getAll()).thenReturn(List.of(entity1, entity2));

		var result = employeeController.getAll();

		verify(employeeService).getAll();
		assertEquals(2, result.getBody().size());
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getById_shouldReturnEmployeeGetDto() {
		var entity = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity.class);
		when(employeeService.getById(1L)).thenReturn(entity);

		var response = employeeController.getById(1L);

		verify(employeeService).getById(1L);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void post_shouldCallServiceAndReturnCreated() {
		EmployeePostDto dto = new EmployeePostDto();
		ResponseEntity<Void> response = employeeController.post(dto);

		verify(employeeService).post(dto);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void put_shouldCallServiceAndReturnOk() {
		EmployeePutDto dto = new EmployeePutDto();
		ResponseEntity<Void> response = employeeController.put(1L, dto);

		verify(employeeService).put(1L, dto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
}
