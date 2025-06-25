package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.EmployeeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.EmployeePostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.EmployeePutDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Tag("integration")
// ai tag: integration
class EmployeeControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean // MISTAKE: MockBean -> MockitoBean (deprecated)
	private EmployeeService employeeService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldGetAllEmployees() throws Exception {
		// MISTAKE: assumed builder and toEntity dto method
		var employee = new EmployeeEntity();
		employee.setId(1L);
		employee.setFirstName("John");
		employee.setLastName("Doe");
		// MISTAKE: required field not set
		employee.setAccessLevel(1);
		Mockito.when(employeeService.getAll()).thenReturn(List.of(employee));

		mockMvc.perform(get("/employees"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1L));
		// INACCURACY: missing field check
	}

	@Test
	void shouldGetEmployeeById() throws Exception {
		// MISTAKE: assumed builder and toEntity dto method
		var employee = new EmployeeEntity();
		employee.setId(2L);
		employee.setFirstName("Jane");
		employee.setLastName("Smith");
		// MISTAKE: required field not set
		employee.setAccessLevel(1);
		Mockito.when(employeeService.getById(2L)).thenReturn(employee);

		mockMvc.perform(get("/employees/2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(2L));
		// INACCURACY: missing field check
	}

	@Test
	void shouldCreateEmployee() throws Exception {
		var postDto = new EmployeePostDto();
		postDto.setFirstName("Alice");
		postDto.setLastName("Brown");
		postDto.setEmail("alice.brown@example.com");
		postDto.setPhoneNumber("123456789");
		// MISTAKE: required field not set
		postDto.setAccessLevel(1);

		mockMvc.perform(post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(postDto)))
			.andExpect(status().isCreated());

		Mockito.verify(employeeService).post(any(EmployeePostDto.class));
	}

	@Test
	void shouldUpdateEmployee() throws Exception {
		var putDto = new EmployeePutDto();
		putDto.setFirstName("Bob");
		putDto.setLastName("Johnson");
		putDto.setEmail("bob.johnson@example.com");
		putDto.setPhoneNumber("987654321");
		// MISTAKE: required field not set
		putDto.setAccessLevel(1);

		mockMvc.perform(put("/employees/3")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(putDto)))
			.andExpect(status().isOk());

		Mockito.verify(employeeService).put(eq(3L), any(EmployeePutDto.class));
	}
}
