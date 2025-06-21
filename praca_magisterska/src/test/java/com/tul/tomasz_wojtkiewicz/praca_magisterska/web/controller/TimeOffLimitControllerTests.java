package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffLimitService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitPutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TimeOffLimitTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TimeOffLimitController.class)
@Tag("controller")
class TimeOffLimitControllerTests {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private TimeOffLimitService timeOffLimitService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void given_missingRequiredRequestParam_when_getAllByYearAndEmployee_then_statusBadRequest() throws Exception {
		mockMvc.perform(get("/time-offs/types/limits?employeeId=1")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/time-offs/types/limits?year=2025")).andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_getAllByYearAndEmployee_then_statusBadRequest(int id) throws Exception {
		mockMvc.perform(get("/time-offs/types/limits?employeeId=%d&year=2025".formatted(id))).andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
	void given_invalidYear_when_getAllByYearAndEmployee_then_statusBadRequest(int year) throws Exception {
		mockMvc.perform(get("/time-offs/types/limits?employeeId=1&year=%d".formatted(year))).andExpect(status().isBadRequest());
	}

	@Test
	void given_validInput_and_serviceThrowsApiExceptionNotFound_when_getAllByYearAndEmployee_then_statusNotFound() throws Exception {
		when(timeOffLimitService.getAllByYearAndEmployeeId(2025, 1)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, ""));
		mockMvc.perform(get("/time-offs/types/limits?employeeId=1&year=2025")).andExpect(status().isNotFound());
	}

	@Test
	void given_validInput_and_serviceReturnsListOfLimits_when_getAllByYearAndEmployee_then_statusOk_and_returnsListOfLimits() throws Exception {
		var employee = EmployeeTestEntityFactory.build().asEntity();
		var type = TimeOffTypeTestEntityFactory.build().asEntity();
		var limit = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity();
		when(timeOffLimitService.getAllByYearAndEmployeeId(2025, 1)).thenReturn(List.of(limit));
		mockMvc.perform(get("/time-offs/types/limits?employeeId=1&year=2025"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].id").value(limit.getId()))
			.andExpect(jsonPath("$[0].maxHours").value(limit.getMaxHours()))
			.andExpect(jsonPath("$[0].leaveYear").value(limit.getLeaveYear()))
			.andExpect(jsonPath("$[0].typeId").value(type.getId()))
			.andExpect(jsonPath("$[0].employeeId").value(employee.getId()));
	}

	@Test
	void given_invalidDto_when_putAll_then_statusBadRequest() throws Exception {
		var dto = TimeOffLimitPutTestDtoFactory.builder().typeId(-1L).employeeId(1L).build().asDto();
		mockMvc.perform(
			put("/time-offs/types/limits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionNotFound_when_putAll_then_statusNotFound() throws Exception {
		var dto = TimeOffLimitPutTestDtoFactory.builder().typeId(1L).employeeId(1L).build().asDto();
		doThrow(new ApiException(HttpStatus.NOT_FOUND, "")).when(timeOffLimitService).putAll(List.of(dto));
		mockMvc.perform(
			put("/time-offs/types/limits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))
		).andExpect(status().isNotFound());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionConflict_when_putAll_then_statusNotFound() throws Exception {
		var dto = TimeOffLimitPutTestDtoFactory.builder().typeId(1L).employeeId(1L).build().asDto();
		doThrow(new ApiException(HttpStatus.CONFLICT, "")).when(timeOffLimitService).putAll(List.of(dto));
		mockMvc.perform(
			put("/time-offs/types/limits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))
		).andExpect(status().isConflict());
	}

	@Test
	void given_validDto_and_serviceDoesNotThrow_when_putAll_then_statusCreated() throws Exception {
		var dto = TimeOffLimitPutTestDtoFactory.builder().typeId(1L).employeeId(1L).build().asDto();
		mockMvc.perform(
			put("/time-offs/types/limits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))
		).andExpect(status().isCreated());
	}
}
