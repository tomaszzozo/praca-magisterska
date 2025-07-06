package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller;// Testy jednostkowe dla TimeOffController

import com.tul.tomasz_wojtkiewicz.praca_magisterska.service.TimeOffService;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get.TimeOffGetDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffPutDto;
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
class TimeOffControllerTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.web.controller'

	private TimeOffService timeOffService;
	private TimeOffController timeOffController;

	@BeforeEach
	void setUp() {
		timeOffService = mock(TimeOffService.class);
		timeOffController = new TimeOffController(timeOffService);
	}

	@Test
		// cases: 1
	void getAllByYearAndEmployeeId_shouldReturnListOfTimeOffGetDto() {
		var entity1 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity.class, Mockito.RETURNS_DEEP_STUBS);
		var entity2 = Mockito.mock(com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity.class, Mockito.RETURNS_DEEP_STUBS);
		when(entity1.getTimeOffType().getId()).thenReturn(1L);
		when(entity2.getTimeOffType().getId()).thenReturn(1L);
		when(timeOffService.getAllByYearAndEmployeeId(2023, 1L)).thenReturn(List.of(entity1, entity2));

		ResponseEntity<List<TimeOffGetDto>> response = timeOffController.getAllByYearAndEmployeeId(2023, 1L); // MISTAKE: missing deep stubs cause null pointer exception

		verify(timeOffService).getAllByYearAndEmployeeId(2023, 1L);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		// INACCURACY: only sized checked, not actual content
		assertEquals(2, response.getBody().size());
	}

	@Test
		// cases: 1
	void post_shouldCallServiceAndReturnCreated() {
		TimeOffPostDto dto = new TimeOffPostDto();

		ResponseEntity<Void> response = timeOffController.post(dto);

		verify(timeOffService).post(dto);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
		// cases: 1
	void put_shouldCallServiceAndReturnCreated() {
		TimeOffPutDto dto = new TimeOffPutDto();

		ResponseEntity<Void> response = timeOffController.put(1L, dto);

		verify(timeOffService).put(1L, dto);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
		// cases: 1
	void delete_shouldCallServiceAndReturnOk() {
		ResponseEntity<Void> response = timeOffController.delete(1L);

		verify(timeOffService).delete(1L);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
}
