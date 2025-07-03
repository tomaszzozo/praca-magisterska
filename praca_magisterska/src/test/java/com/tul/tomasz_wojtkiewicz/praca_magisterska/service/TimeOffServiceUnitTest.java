// Testy jednostkowe TimeOffService
package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffPutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class TimeOffServiceUnitTest {

	private TimeOffRepository timeOffRepository;
	private TimeOffLimitService timeOffLimitService;
	private TimeOffTypeService timeOffTypeService;
	private EmployeeService employeeService;
	private TimeOffService timeOffService;

	@BeforeEach
	void setUp() {
		timeOffRepository = mock(TimeOffRepository.class);
		timeOffLimitService = mock(TimeOffLimitService.class);
		timeOffTypeService = mock(TimeOffTypeService.class);
		employeeService = mock(EmployeeService.class);
		timeOffService = new TimeOffService(timeOffRepository, timeOffLimitService, timeOffTypeService, employeeService);
	}

	@Test
	void getAllByYearAndEmployeeId_ShouldReturnList() {
		long employeeId = 1L;
		int year = 2025;
		TimeOffEntity entity = new TimeOffEntity();
		entity.setId(10L);

		when(timeOffRepository.findAllByYearAndEmployeeId(employeeId, year)).thenReturn(List.of(entity));

		List<TimeOffEntity> result = timeOffService.getAllByYearAndEmployeeId(year, employeeId);

		assertEquals(1, result.size());
		assertEquals(entity, result.get(0));
	}

	@Test
	void post_ShouldSaveNewTimeOff_WhenNoConflictsAndWithinLimit() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 5));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setFirstDay(LocalDate.of(2025, 1, 1));
		existing.setLastDayInclusive(LocalDate.of(2025, 1, 2));
		existing.setHoursCount(5);
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(100L);
		existing.setTimeOffType(type);

		when(timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear())).thenReturn(List.of(existing));

		TimeOffLimitEntity yearlyLimit = new TimeOffLimitEntity();
		yearlyLimit.setMaxHours(20);

		when(timeOffLimitService.getById(dto.getYearlyLimitId())).thenReturn(yearlyLimit);

		TimeOffTypeEntity timeOffType = new TimeOffTypeEntity();
		timeOffType.setId(dto.getTypeId());

		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(dto.getEmployeeId());

		when(timeOffTypeService.getById(dto.getTypeId())).thenReturn(timeOffType);
		when(employeeService.getById(dto.getEmployeeId())).thenReturn(employee);

		timeOffService.post(dto);

		ArgumentCaptor<TimeOffEntity> captor = ArgumentCaptor.forClass(TimeOffEntity.class);
		verify(timeOffRepository).save(captor.capture());
		TimeOffEntity saved = captor.getValue();

		assertEquals(dto.getFirstDay(), saved.getFirstDay());
		assertEquals(dto.getLastDayInclusive(), saved.getLastDayInclusive());
		assertEquals(dto.getHoursCount(), saved.getHoursCount());
		assertEquals(timeOffType, saved.getTimeOffType());
		assertEquals(employee, saved.getEmployee());
		assertEquals(yearlyLimit, saved.getTimeOffYearlyLimit());
	}

	@Test
	void post_ShouldThrowApiException_WhenOverlappingTimeOffExists() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2025, 6, 3));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 7));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity overlapping = new TimeOffEntity();
		overlapping.setFirstDay(LocalDate.of(2025, 6, 1));
		overlapping.setLastDayInclusive(LocalDate.of(2025, 6, 5));

		when(timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear())).thenReturn(List.of(overlapping));

		ApiException ex = assertThrows(ApiException.class, () -> timeOffService.post(dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Nowy urlop koliduje z istniejącym urlopem.", ex.getMessage());

		verify(timeOffRepository, never()).save(any());
	}

	@Test
	void post_ShouldThrowApiException_WhenExceedsLimit() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 5));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setFirstDay(LocalDate.of(2025, 1, 1));
		existing.setLastDayInclusive(LocalDate.of(2025, 1, 2));
		existing.setHoursCount(15);
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(100L);
		existing.setTimeOffType(type);

		when(timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear())).thenReturn(List.of(existing));

		TimeOffLimitEntity yearlyLimit = new TimeOffLimitEntity();
		yearlyLimit.setMaxHours(20);

		when(timeOffLimitService.getById(dto.getYearlyLimitId())).thenReturn(yearlyLimit);

		ApiException ex = assertThrows(ApiException.class, () -> timeOffService.post(dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertTrue(ex.getMessage().contains("Nowy urlop przekorczy limit o"));

		verify(timeOffRepository, never()).save(any());
	}

	@Test
	void put_ShouldUpdateTimeOff_WhenValid() {
		long id = 5L;
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 5));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setId(id);
		existing.setFirstDay(LocalDate.of(2025, 6, 1));
		existing.setLastDayInclusive(LocalDate.of(2025, 6, 3));
		existing.setHoursCount(5);
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(100L);
		existing.setTimeOffType(type);

		when(timeOffRepository.findById(id)).thenReturn(Optional.of(existing));

		TimeOffEntity other = new TimeOffEntity();
		other.setId(10L);
		other.setFirstDay(LocalDate.of(2025, 1, 1));
		other.setLastDayInclusive(LocalDate.of(2025, 1, 2));
		other.setHoursCount(5);
		other.setTimeOffType(type);

		when(timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear())).thenReturn(List.of(existing, other));

		TimeOffLimitEntity yearlyLimit = new TimeOffLimitEntity();
		yearlyLimit.setMaxHours(20);
		when(timeOffLimitService.getById(dto.getYearlyLimitId())).thenReturn(yearlyLimit);

		TimeOffTypeEntity timeOffType = new TimeOffTypeEntity();
		timeOffType.setId(dto.getTypeId());

		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(dto.getEmployeeId());

		when(timeOffTypeService.getById(dto.getTypeId())).thenReturn(timeOffType);
		when(employeeService.getById(dto.getEmployeeId())).thenReturn(employee);

		timeOffService.put(id, dto);

		ArgumentCaptor<TimeOffEntity> captor = ArgumentCaptor.forClass(TimeOffEntity.class);
		verify(timeOffRepository).save(captor.capture());
		TimeOffEntity saved = captor.getValue();

		assertEquals(dto.getFirstDay(), saved.getFirstDay());
		assertEquals(dto.getLastDayInclusive(), saved.getLastDayInclusive());
		assertEquals(dto.getHoursCount(), saved.getHoursCount());
		assertEquals(timeOffType, saved.getTimeOffType());
		assertEquals(employee, saved.getEmployee());
		assertEquals(yearlyLimit, saved.getTimeOffYearlyLimit());
	}

	@Test
	void put_ShouldThrowApiException_WhenYearChanged() {
		long id = 5L;
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2024, 6, 1)); // year different than existing
		dto.setLastDayInclusive(LocalDate.of(2024, 6, 5));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setId(id);
		existing.setFirstDay(LocalDate.of(2025, 6, 1));

		when(timeOffRepository.findById(id)).thenReturn(Optional.of(existing));

		ApiException ex = assertThrows(ApiException.class, () -> timeOffService.put(id, dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Nie można modyfikować roku urlopu.", ex.getMessage());

		verify(timeOffRepository, never()).save(any());
	}

	@Test
	void put_ShouldThrowApiException_WhenOverlappingExists() {
		long id = 5L;
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 5));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setId(id);
		existing.setFirstDay(LocalDate.of(2025, 6, 1));

		TimeOffEntity other = new TimeOffEntity();
		other.setId(10L);
		other.setFirstDay(LocalDate.of(2025, 6, 3));
		other.setLastDayInclusive(LocalDate.of(2025, 6, 7));

		when(timeOffRepository.findById(id)).thenReturn(Optional.of(existing));
		when(timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear())).thenReturn(List.of(existing, other));

		ApiException ex = assertThrows(ApiException.class, () -> timeOffService.put(id, dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Nowy urlop koliduje z istniejącym urlopem.", ex.getMessage());

		verify(timeOffRepository, never()).save(any());
	}

	@Test
	void put_ShouldThrowApiException_WhenExceedsLimit() {
		long id = 5L;
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2025, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2025, 6, 5));
		dto.setHoursCount(10);
		dto.setTypeId(100L);
		dto.setYearlyLimitId(200L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setId(id);
		existing.setFirstDay(LocalDate.of(2025, 6, 1));

		TimeOffEntity other = new TimeOffEntity();
		other.setId(10L);
		other.setFirstDay(LocalDate.of(2025, 1, 1));
		other.setLastDayInclusive(LocalDate.of(2025, 1, 2));
		other.setHoursCount(15);
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(100L);
		other.setTimeOffType(type);

		when(timeOffRepository.findById(id)).thenReturn(Optional.of(existing));
		when(timeOffRepository.findAllByYearAndEmployeeId(dto.getEmployeeId(), dto.getFirstDay().getYear())).thenReturn(List.of(existing, other));

		TimeOffLimitEntity yearlyLimit = new TimeOffLimitEntity();
		yearlyLimit.setMaxHours(20);
		when(timeOffLimitService.getById(dto.getYearlyLimitId())).thenReturn(yearlyLimit);

		ApiException ex = assertThrows(ApiException.class, () -> timeOffService.put(id, dto));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertTrue(ex.getMessage().contains("Nowy urlop przekorczy limit o"));

		verify(timeOffRepository, never()).save(any());
	}

	@Test
	void delete_ShouldDeleteExistingEntity() {
		long id = 1L;
		TimeOffEntity entity = new TimeOffEntity();
		entity.setId(id);

		when(timeOffRepository.findById(id)).thenReturn(Optional.of(entity));

		timeOffService.delete(id);

		verify(timeOffRepository).delete(entity);
	}

	@Test
	void delete_ShouldThrowApiException_WhenNotFound() {
		long id = 1L;

		when(timeOffRepository.findById(id)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> timeOffService.delete(id));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
		assertEquals("Nie znaleziono urlopu", ex.getMessage());

		verify(timeOffRepository, never()).delete(any());
	}
}
