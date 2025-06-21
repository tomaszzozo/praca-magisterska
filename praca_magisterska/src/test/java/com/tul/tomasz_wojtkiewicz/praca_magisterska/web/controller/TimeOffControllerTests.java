package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.EmployeeTestEntityFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off.TimeOffTestEntityFactory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TimeOffController.class)
@Tag("controller")
class TimeOffControllerTests {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private TimeOffService timeOffService;
	@Autowired
	private ObjectMapper objectMapper;

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_getAllByYearAndEmployee_then_statusBadRequest(int id) throws Exception {
		mockMvc.perform(get("/time-offs?employeeId=%d&year=2025".formatted(id))).andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#years")
	void given_invalidYear_when_getAllByYearAndEmployee_then_statusBadRequest(int year) throws Exception {
		mockMvc.perform(get("/time-offs?employeeId=1&year=%d".formatted(year))).andExpect(status().isBadRequest());
	}

	@Test
	void given_validInput_and_serviceReturnsEmptyList_when_getAllByYearAndEmployee_then_statusOk_and_returnsEmptyList() throws Exception {
		when(timeOffService.getAllByYearAndEmployeeId(2025, 1)).thenReturn(List.of());
		mockMvc.perform(get("/time-offs?employeeId=1&year=2025"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}

	@Test
	void given_validInput_and_serviceReturnsListOfTimeOffs_when_getAllByYearAndEmployee_then_statusOk_and_returnsListOfTimeOffs() throws Exception {
		var employee = EmployeeTestEntityFactory.build().asEntity();
		var type = TimeOffTypeTestEntityFactory.build().asEntity();
		var limit = TimeOffLimitTestEntityFactory.builder().employee(employee).timeOffType(type).build().asEntity();
		var timeOff = TimeOffTestEntityFactory.builder().employee(employee).timeOffType(type).timeOffYearlyLimit(limit).build().asEntity();
		when(timeOffService.getAllByYearAndEmployeeId(2025, 1)).thenReturn(List.of(timeOff));
		mockMvc.perform(get("/time-offs?employeeId=1&year=2025"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].id").value(timeOff.getId()))
			.andExpect(jsonPath("$[0].firstDay").value(timeOff.getFirstDay().toString()))
			.andExpect(jsonPath("$[0].lastDayInclusive").value(timeOff.getLastDayInclusive().toString()))
			.andExpect(jsonPath("$[0].hoursCount").value(timeOff.getHoursCount()))
			.andExpect(jsonPath("$[0].typeId").value(type.getId()))
			.andExpect(jsonPath("$[0].yearlyLimitId").value(limit.getId()))
			.andExpect(jsonPath("$[0].employeeId").value(employee.getId()))
			.andExpect(jsonPath("$[0].comment").value(timeOff.getComment()));
	}

	@Test
	void given_invalidDto_when_post_then_statusBadRequest() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(-1L).build().asPostDto();
		mockMvc.perform(
			post("/time-offs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionConflict_when_post_then_statusConflict() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPostDto();
		doThrow(new ApiException(HttpStatus.CONFLICT, "")).when(timeOffService).post(dto);
		mockMvc.perform(
			post("/time-offs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isConflict());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionNotFound_when_post_then_statusNotFound() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPostDto();
		doThrow(new ApiException(HttpStatus.NOT_FOUND, "")).when(timeOffService).post(dto);
		mockMvc.perform(
			post("/time-offs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isNotFound());
	}

	@Test
	void given_validDto_and_serviceDoesNotThrow_when_post_then_statusCreated() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPostDto();
		mockMvc.perform(
			post("/time-offs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isCreated());
	}

	@Test
	void given_invalidDto_when_put_then_statusBadRequest() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(-1L).build().asPutDto();
		mockMvc.perform(
			put("/time-offs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_put_then_statusBadRequest(int id) throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPutDto();
		mockMvc.perform(
			put("/time-offs/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionConflict_when_put_then_statusConflict() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPutDto();
		doThrow(new ApiException(HttpStatus.CONFLICT, "")).when(timeOffService).put(1, dto);
		mockMvc.perform(
			put("/time-offs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isConflict());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionNotFound_when_put_then_statusNotFound() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPutDto();
		doThrow(new ApiException(HttpStatus.NOT_FOUND, "")).when(timeOffService).put(1, dto);
		mockMvc.perform(
			put("/time-offs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isNotFound());
	}

	@Test
	void given_validDto_and_serviceDoesNotThrow_when_post_put_statusCreated() throws Exception {
		var dto = TimeOffTestDtoFactory.builder().typeId(1L).employeeId(1L).yearlyLimitId(1L).build().asPutDto();
		mockMvc.perform(
			put("/time-offs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
		).andExpect(status().isCreated());
	}



	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#integerNegativeAndZero")
	void given_invalidId_when_delete_then_statusBadRequest(int id) throws Exception {
		mockMvc.perform(delete("/time-offs/"+id)).andExpect(status().isBadRequest());
	}

	@Test
	void given_validId_and_serviceThrowsApiExceptionNotFound_when_delete_then_statusNotFound() throws Exception {
		doThrow(new ApiException(HttpStatus.NOT_FOUND, "")).when(timeOffService).delete(1);
		mockMvc.perform(delete("/time-offs/1")).andExpect(status().isNotFound());
	}

	@Test
	void given_validId_and_serviceDoesNotThrow_when_delete_then_statusOk() throws Exception {
		mockMvc.perform(delete("/time-offs/1")).andExpect(status().isOk());
	}
}
