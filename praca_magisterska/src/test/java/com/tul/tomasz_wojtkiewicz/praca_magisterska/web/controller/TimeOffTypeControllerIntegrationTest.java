package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffTypeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
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

@WebMvcTest(TimeOffTypeController.class)
@Tag("integration")
// ai tag: integration
class TimeOffTypeControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean // MISTAKE: MockBean -> MockitoBean (deprecated)
	private TimeOffTypeService timeOffTypeService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldGetAllTimeOffTypes() throws Exception {
		// MISTAKE: assumed builder and toEntity dto method
		var entity = new TimeOffTypeEntity();
		entity.setId(1L);
		entity.setName("Vacation");
		// MISTAKE: required field not set
		entity.setCompensationPercentage(0.85f);
		Mockito.when(timeOffTypeService.getAll()).thenReturn(List.of(entity));

		mockMvc.perform(get("/time-offs/types"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].name").value("Vacation"));
		// INACCURACY: missing field check
	}

	@Test
	void shouldPutAllTimeOffTypes() throws Exception {
		var putDto = new TimeOffTypePutDto();
		putDto.setId(1L);
		putDto.setName("Sick Leave");
		// MISTAKE: required field not set
		putDto.setCompensationPercentage(0.8f);

		mockMvc.perform(put("/time-offs/types")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(putDto))))
			.andExpect(status().isCreated());

		Mockito.verify(timeOffTypeService).putAll(anyList());
	}
}
