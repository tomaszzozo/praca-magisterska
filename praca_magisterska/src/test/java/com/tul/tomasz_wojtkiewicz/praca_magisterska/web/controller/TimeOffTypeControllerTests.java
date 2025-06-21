package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffTypeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypePutTestDtoFactory;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TimeOffTypeTestEntityFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TimeOffTypeController.class)
@Tag("controller")
class TimeOffTypeControllerTests {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private TimeOffTypeService timeOffTypeService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void given_serviceReturnsListWithTimeOffType_when_getAll_then_statusOk_and_returnsListWithTimeOffType() throws Exception {
		var entity = TimeOffTypeTestEntityFactory.build().asEntity();
		when(timeOffTypeService.getAll()).thenReturn(List.of(entity));
		mockMvc.perform(get("/time-offs/types"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].id").value(0))
			.andExpect(jsonPath("$[0].name").value(entity.getName()))
			.andExpect(jsonPath("$[0].compensationPercentage").value(entity.getCompensationPercentage()));
	}

	@Test
	void given_serviceReturnsEmptyList_when_getAll_then_statusOk_and_returnsEmptyList() throws Exception {
		when(timeOffTypeService.getAll()).thenReturn(List.of());
		mockMvc.perform(get("/time-offs/types"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}

	@Test
	void given_invalidDto_when_putAll_then_statusBadRequest() throws Exception {
		var dto = TimeOffTypePutTestDtoFactory.builder().id(-1L).build().asDto();
		mockMvc.perform(
			put("/time-offs/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto))
			)
		).andExpect(status().isBadRequest());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionConflict_when_putAll_then_statusConflict() throws Exception {
		var dto = TimeOffTypePutTestDtoFactory.build().asDto();
		doThrow(new ApiException(HttpStatus.CONFLICT, "")).when(timeOffTypeService).putAll(List.of(dto));
		mockMvc.perform(
			put("/time-offs/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto))
				)
		).andExpect(status().isConflict());
	}

	@Test
	void given_validDto_and_serviceThrowsApiExceptionNotFound_when_putAll_then_statusNotFound() throws Exception {
		var dto = TimeOffTypePutTestDtoFactory.build().asDto();
		doThrow(new ApiException(HttpStatus.NOT_FOUND, "")).when(timeOffTypeService).putAll(List.of(dto));
		mockMvc.perform(
			put("/time-offs/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto))
				)
		).andExpect(status().isNotFound());
	}
}
