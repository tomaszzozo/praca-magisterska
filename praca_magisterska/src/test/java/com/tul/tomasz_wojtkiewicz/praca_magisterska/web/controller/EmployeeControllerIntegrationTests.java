package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.EmployeeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeePostTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeePutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
@Tag("controller")
@Tag("integration")
class EmployeeControllerIntegrationTests {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private EmployeeService employeeService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void given_serviceReturnsListWithEmployee_when_getAll_then_returnsListWithEmployee_and_statusOk() throws Exception {
		var entity = EmployeeTestEntityFactory.build().asEntity();
		when(employeeService.getAll()).thenReturn(List.of(entity));
		mockMvc.perform(get("/employees"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].id").value(entity.getId()))
			.andExpect(jsonPath("$[0].email").value(entity.getEmail()))
			.andExpect(jsonPath("$[0].phoneNumber").value(entity.getPhoneNumber()))
			.andExpect(jsonPath("$[0].accessLevel").value(entity.getAccessLevel()))
			.andExpect(jsonPath("$[0].firstName").value(entity.getFirstName()))
			.andExpect(jsonPath("$[0].lastName").value(entity.getLastName()));
	}

	@Test
	void given_serviceReturnsEmptyList_when_getAll_then_returnsEmptyList_and_statusOk() throws Exception {
		when(employeeService.getAll()).thenReturn(List.of());
		mockMvc.perform(get("/employees"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_getById_then_statusBadRequest(int id) throws Exception {
		mockMvc.perform(get("/employees/" + id)).andExpect(status().isBadRequest());
	}

	@Test
	void given_serviceThrowsApiExceptionNotFound_when_getById_then_statusBadRequest() throws Exception {
		when(employeeService.getById(1)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, ""));
		mockMvc.perform(get("/employees/1")).andExpect(status().isNotFound());
	}

	@Test
	void given_serviceDoesNotThrow_when_getById_then_statusOk_and_returnsEmployee() throws Exception {
		var entity = EmployeeTestEntityFactory.build().asEntity();
		when(employeeService.getById(1)).thenReturn(entity);
		mockMvc.perform(get("/employees/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(entity.getId()))
			.andExpect(jsonPath("$.email").value(entity.getEmail()))
			.andExpect(jsonPath("$.phoneNumber").value(entity.getPhoneNumber()))
			.andExpect(jsonPath("$.accessLevel").value(entity.getAccessLevel()))
			.andExpect(jsonPath("$.firstName").value(entity.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(entity.getLastName()));
	}

	@Test
	void given_invalidDto_when_post_then_statusBadRequest() throws Exception {
		var dto = EmployeePostTestDtoFactory.builder().phoneNumber("whoops").build().asDto();
		mockMvc.perform(
			post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionConflict_when_post_then_statusConflict() throws Exception {
		var dto = EmployeePostTestDtoFactory.build().asDto();
		doThrow(new ApiException(HttpStatus.CONFLICT, "")).when(employeeService).post(dto);
		mockMvc.perform(
			post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isConflict());
	}

	@Test
	void given_validDto_and_serviceDoesNotThrow_when_post_then_statusCreated() throws Exception {
		var dto = EmployeePostTestDtoFactory.build().asDto();
		mockMvc.perform(
			post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isCreated());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_put_then_statusBadRequest(int id) throws Exception {
		var dto = EmployeePutTestDtoFactory.build().asDto();
		mockMvc.perform(
			put("/employees/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_invalidDto_when_put_then_statusBadRequest() throws Exception {
		var dto = EmployeePutTestDtoFactory.builder().phoneNumber("whoops").build().asDto();
		mockMvc.perform(
			put("/employees/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_validInput_and_serviceThrowsApiExceptionConflict_when_put_then_statusConflict() throws Exception {
		var dto = EmployeePutTestDtoFactory.builder().build().asDto();
		doThrow(new ApiException(HttpStatus.CONFLICT, "")).when(employeeService).put(1L, dto);
		mockMvc.perform(
			put("/employees/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isConflict());
	}

	@Test
	void given_validInput_and_serviceDoesNotThrow_when_put_then_statusOk() throws Exception {
		var dto = EmployeePutTestDtoFactory.builder().build().asDto();
		mockMvc.perform(
			put("/employees/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isOk());
	}
}
