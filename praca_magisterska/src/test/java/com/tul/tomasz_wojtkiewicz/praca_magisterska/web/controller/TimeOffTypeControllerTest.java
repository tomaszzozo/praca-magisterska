package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;// Testy jednostkowe dla TimeOffTypeController

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffTypeService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffTypeGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
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
class TimeOffTypeControllerTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller'

	private TimeOffTypeService timeOffTypeService;
	private TimeOffTypeController timeOffTypeController;

	@BeforeEach
	void setUp() {
		timeOffTypeService = mock(TimeOffTypeService.class);
		timeOffTypeController = new TimeOffTypeController(timeOffTypeService);
	}

	@Test
		// CASES: 1
	void getAll_shouldReturnListOfTimeOffTypeGetDto() {
		var entity1 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity.class);
		var entity2 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity.class);
		when(timeOffTypeService.getAll()).thenReturn(List.of(entity1, entity2));

		ResponseEntity<List<TimeOffTypeGetDto>> response = timeOffTypeController.getAll();

		verify(timeOffTypeService).getAll();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		// INACCURACY: only sized checked, not actual content
		assertEquals(2, response.getBody().size());
	}

	@Test
		// CASES: 1
	void putAll_shouldCallServiceAndReturnCreated() {
		TimeOffTypePutDto dto1 = new TimeOffTypePutDto();
		TimeOffTypePutDto dto2 = new TimeOffTypePutDto();
		List<TimeOffTypePutDto> dtos = List.of(dto1, dto2);

		ResponseEntity<Void> response = timeOffTypeController.putAll(dtos);

		verify(timeOffTypeService).putAll(dtos);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNull(response.getBody());
	}
}
