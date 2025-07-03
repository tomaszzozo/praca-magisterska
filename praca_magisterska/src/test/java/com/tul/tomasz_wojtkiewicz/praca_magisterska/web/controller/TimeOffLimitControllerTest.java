package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;// Testy jednostkowe dla TimeOffLimitController

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffLimitService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffLimitGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class TimeOffLimitControllerTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller'

	private TimeOffLimitService timeOffLimitService;
	private TimeOffLimitController timeOffLimitController;

	@BeforeEach
	void setUp() {
		timeOffLimitService = mock(TimeOffLimitService.class);
		timeOffLimitController = new TimeOffLimitController(timeOffLimitService);
	}

	@Test
		// CASES: 1
	void getAllByYearAndEmployee_shouldReturnListOfTimeOffLimitGetDto() {
		var entity1 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity.class, Mockito.RETURNS_DEEP_STUBS);
		var entity2 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity.class, Mockito.RETURNS_DEEP_STUBS);
		when(entity1.getTimeOffType().getId()).thenReturn(1L);
		when(entity2.getTimeOffType().getId()).thenReturn(1L);
		when(timeOffLimitService.getAllByYearAndEmployeeId(2025, 10L)).thenReturn(List.of(entity1, entity2));

		ResponseEntity<List<TimeOffLimitGetDto>> response = timeOffLimitController.getAllByYearAndEmployee(2025, 10L); // MISTAKE: missing deep stubs returning non-null id

		verify(timeOffLimitService).getAllByYearAndEmployeeId(2025, 10L);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
	}

	@Test
		// CASES: 1
	void putAll_shouldCallServiceAndReturnCreated() {
		TimeOffLimitPutDto dto1 = new TimeOffLimitPutDto();
		TimeOffLimitPutDto dto2 = new TimeOffLimitPutDto();
		List<TimeOffLimitPutDto> dtos = List.of(dto1, dto2);

		ResponseEntity<Void> response = timeOffLimitController.putAll(dtos);

		verify(timeOffLimitService).putAll(dtos);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNull(response.getBody());
	}
}
