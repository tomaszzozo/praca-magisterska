package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffPutDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeOffController.class)
@Tag("integration")
// ai tag: integration
class TimeOffControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean // MISTAKE: MockBean -> MockitoBean (deprecated)
	private TimeOffService timeOffService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
		// cases: 1
	void shouldGetAllByYearAndEmployeeId() throws Exception {
		var mockedEmployee = Mockito.mock(EmployeeEntity.class);
		Mockito.doReturn(5L).when(mockedEmployee).getId();
		var mockedType = Mockito.mock(TimeOffTypeEntity.class);
		Mockito.when(mockedType.getId()).thenReturn(1L);
		var mockedYearlyLimit = Mockito.mock(TimeOffLimitEntity.class);
		Mockito.when(mockedYearlyLimit.getId()).thenReturn(1L);

		// MISTAKE: assumed builder and toEntity dto method
		var entity = new TimeOffEntity();
		entity.setId(1L);
		entity.setEmployee(mockedEmployee);
		entity.setFirstDay(LocalDate.of(2025, 1, 10));
		entity.setLastDayInclusive(LocalDate.of(2025, 1, 15));
		entity.setTimeOffType(mockedType);
		// MISTAKE: required fields not set
		entity.setHoursCount(1);
		entity.setTimeOffYearlyLimit(mockedYearlyLimit);
		Mockito.when(timeOffService.getAllByYearAndEmployeeId(2025, 5L))
			.thenReturn(List.of(entity));

		mockMvc.perform(get("/time-offs")
				.param("year", "2025")
				.param("employeeId", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].employeeId").value(5))
			// INACCURACY: missing fields check
//			.andExpect(jsonPath("$[0].type").value("VACATION")); MISTAKE: invalid json structure
			.andExpect(jsonPath("$[0].typeId").value(1));
	}

	@Test
		// cases: 1
	void shouldPostTimeOff() throws Exception {
		TimeOffPostDto postDto = new TimeOffPostDto();
		postDto.setEmployeeId(5L);
		postDto.setTypeId(1L);
		postDto.setFirstDay(LocalDate.of(2025, 1, 10)); // MISTAKE: invalid setter name setFrom
		postDto.setLastDayInclusive(LocalDate.of(2025, 1, 15)); // MISTAKE: invalid setter name setTo
		// MISTAKE: required fields not set
		postDto.setComment("");
		postDto.setHoursCount(1);
		postDto.setYearlyLimitId(1L);

		mockMvc.perform(post("/time-offs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(postDto)))
			.andExpect(status().isCreated());

		Mockito.verify(timeOffService).post(any(TimeOffPostDto.class));
	}

	@Test
		// cases: 1
	void shouldPutTimeOff() throws Exception {
		TimeOffPutDto putDto = new TimeOffPutDto();
		putDto.setTypeId(2L);
		putDto.setFirstDay(LocalDate.of(2025, 1, 20)); // MISTAKE: invalid setter name setFrom
		putDto.setLastDayInclusive(LocalDate.of(2025, 1, 22)); // MISTAKE: invalid setter name setTo
		// MISTAKE: required fields not set
		putDto.setComment("");
		putDto.setHoursCount(1);
		putDto.setYearlyLimitId(1L);
		putDto.setEmployeeId(1L);

		mockMvc.perform(put("/time-offs/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(putDto)))
			.andExpect(status().isCreated());

		Mockito.verify(timeOffService).put(1L, putDto);
	}

	@Test
		// cases: 1
	void shouldDeleteTimeOff() throws Exception {
		mockMvc.perform(delete("/time-offs/1"))
			.andExpect(status().isOk());

		Mockito.verify(timeOffService).delete(1L);
	}
}
