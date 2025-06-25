package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffLimitService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeOffLimitController.class)
@Tag("integration")
// ai tag: integration
class TimeOffLimitControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean // MISTAKE: MockBean -> MockitoBean (deprecated)
	private TimeOffLimitService timeOffLimitService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
		// cases: 1
	void shouldGetAllByYearAndEmployee() throws Exception {
		var mockedEmployee = Mockito.mock(EmployeeEntity.class);
		Mockito.doReturn(10L).when(mockedEmployee).getId();
		var mockedType = Mockito.mock(TimeOffTypeEntity.class);
		Mockito.doReturn(5L).when(mockedType).getId();

		// MISTAKE: assumed builder and toEntity dto method
		var entity = new TimeOffLimitEntity();
		entity.setId(1L);
		entity.setLeaveYear(2025);
		entity.setEmployee(mockedEmployee);
		entity.setMaxHours(20);
		// MISTAKE: required field not set
		entity.setTimeOffType(mockedType);
		Mockito.when(timeOffLimitService.getAllByYearAndEmployeeId(2025, 10L)).thenReturn(List.of(entity));

		mockMvc.perform(get("/time-offs/types/limits")
				.param("year", "2025")
				.param("employeeId", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1L))
//			.andExpect(jsonPath("$[0].year").value(2025)) MISTAKE: invalid field name
			.andExpect(jsonPath("$[0].leaveYear").value(2025))
			.andExpect(jsonPath("$[0].employeeId").value(10))
//			.andExpect(jsonPath("$[0].limit").value(20)) MISTAKE: invalid field name
			.andExpect(jsonPath("$[0].maxHours").value(20));
	}

	@Test
		// cases: 1
	void shouldPutAllTimeOffLimits() throws Exception {
		var putDto = new TimeOffLimitPutDto();
		putDto.setId(1L);
		putDto.setYear(2025);
		putDto.setEmployeeId(10L);
		putDto.setMaxHours(15); // MISTAKE: invalid setter name setLimit
		putDto.setTypeId(1L); // MISTAKE: required field not set

		mockMvc.perform(put("/time-offs/types/limits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(putDto))))
			.andExpect(status().isCreated());

		Mockito.verify(timeOffLimitService).putAll(anyList());
	}
}
