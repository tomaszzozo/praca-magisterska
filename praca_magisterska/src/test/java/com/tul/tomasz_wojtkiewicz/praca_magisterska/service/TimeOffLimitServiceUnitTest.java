// Testy jednostkowe TimeOffLimitService
package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito; // INACCURACY: unused import
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class TimeOffLimitServiceUnitTest {

	private TimeOffLimitRepository timeOffLimitRepository;
	private TimeOffTypeService timeOffTypeService;
	private EmployeeService employeeService;
	private TimeOffLimitService timeOffLimitService;

	@BeforeEach
	void setUp() {
		timeOffLimitRepository = mock(TimeOffLimitRepository.class);
		timeOffTypeService = mock(TimeOffTypeService.class);
		employeeService = mock(EmployeeService.class);
		timeOffLimitService = new TimeOffLimitService(timeOffLimitRepository, timeOffTypeService, employeeService);
	}

	@Test
		// cases: 1
	void getAllByYearAndEmployeeId_ShouldReturnDefaultsAndExistingLimits() {
		int year = 2025;
		long employeeId = 1L;

		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(employeeId);

		TimeOffTypeEntity type1 = new TimeOffTypeEntity();
		type1.setId(10L);
		TimeOffTypeEntity type2 = new TimeOffTypeEntity();
		type2.setId(20L);

		List<TimeOffTypeEntity> types = List.of(type1, type2);
		List<TimeOffLimitEntity> existingLimits = new ArrayList<>();

		TimeOffLimitEntity existingLimit = new TimeOffLimitEntity();
		existingLimit.setId(100L);
		existingLimit.setLeaveYear(year);
		existingLimit.setEmployee(employee);
		existingLimit.setTimeOffType(type1);
		existingLimit.setMaxHours(50);

		existingLimits.add(existingLimit);

		when(employeeService.getById(employeeId)).thenReturn(employee);
		when(timeOffTypeService.getAll()).thenReturn(types);
		when(timeOffLimitRepository.findAllByLeaveYearAndEmployeeId(year, employeeId)).thenReturn(existingLimits);

		List<TimeOffLimitEntity> result = timeOffLimitService.getAllByYearAndEmployeeId(year, employeeId);

		// Should contain the existing limit and a default for the missing type2
		assertEquals(2, result.size());

		boolean hasExistingLimit = result.stream().anyMatch(l -> l.getId() != null && l.getId() == 100L); // MISTAKE: id of default limits is null -> null pointer exception
		assertTrue(hasExistingLimit);

		boolean hasDefaultForType2 = result.stream().anyMatch(l -> l.getTimeOffType().getId() == 20L && l.getId() == null);
		assertTrue(hasDefaultForType2);
	}

	@Test
		// cases: 1
	void getById_ShouldReturnEntity_WhenExists() {
		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setId(1L);

		when(timeOffLimitRepository.findById(1L)).thenReturn(Optional.of(entity));

		TimeOffLimitEntity result = timeOffLimitService.getById(1L);
		assertEquals(entity, result);
	}

	@Test
		// cases: 1
	void getById_ShouldThrowApiException_WhenNotFound() {
		when(timeOffLimitRepository.findById(1L)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> timeOffLimitService.getById(1L));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
		assertEquals("Limit urlopu o podanym id nie istnieje", ex.getMessage());
	}

	@Test
		// cases: 1
	void putAll_ShouldSaveNewAndExistingLimits_WhenValid() {
		EmployeeEntity employee = new EmployeeEntity();
		employee.setId(1L);
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(10L);

		TimeOffLimitPutDto newDto = new TimeOffLimitPutDto();
		newDto.setId(0L); // MISTAKE: no L
		newDto.setYear(2025);
		newDto.setEmployeeId(1L);
		newDto.setTypeId(10L);
		newDto.setMaxHours(100);

		TimeOffLimitPutDto existingDto = new TimeOffLimitPutDto();
		existingDto.setId(2L);
		existingDto.setYear(2025);
		existingDto.setEmployeeId(1L);
		existingDto.setTypeId(10L);
		existingDto.setMaxHours(200);

		TimeOffLimitEntity existingEntity = new TimeOffLimitEntity();
		existingEntity.setId(2L);
		existingEntity.setMaxHours(150);
		existingEntity.setTimeOffs(null);

		when(timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(2025, 1L, 10L)).thenReturn(false);
		when(employeeService.getById(1L)).thenReturn(employee);
		when(timeOffTypeService.getById(10L)).thenReturn(type);
		when(timeOffLimitRepository.findById(2L)).thenReturn(Optional.of(existingEntity));

		List<TimeOffLimitPutDto> dtos = List.of(newDto, existingDto);
		timeOffLimitService.putAll(dtos);

		ArgumentCaptor<TimeOffLimitEntity> captor = ArgumentCaptor.forClass(TimeOffLimitEntity.class);
		verify(timeOffLimitRepository, times(2)).save(captor.capture());

		List<TimeOffLimitEntity> saved = captor.getAllValues();

		// Check first saved is new limit with correct values
		TimeOffLimitEntity savedNew = saved.get(0);
		assertEquals(2025, savedNew.getLeaveYear());
		assertEquals(employee, savedNew.getEmployee());
		assertEquals(type, savedNew.getTimeOffType());
		assertEquals(100, savedNew.getMaxHours());

		// Check second saved is updated existing limit
		TimeOffLimitEntity savedExisting = saved.get(1);
		assertEquals(2L, savedExisting.getId());
		assertEquals(200, savedExisting.getMaxHours());
	}

	@Test
		// cases: 1
	void putAll_ShouldThrowApiException_WhenDuplicateLimitExists() {
		TimeOffLimitPutDto newDto = new TimeOffLimitPutDto();
		newDto.setId(0L); // MISTAKE: no L
		newDto.setYear(2025);
		newDto.setEmployeeId(1L);
		newDto.setTypeId(10L);
		newDto.setMaxHours(100);

		when(timeOffLimitRepository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(2025, 1L, 10L)).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(newDto)));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Limit dla danego roku pracownika i typu urlopu już istnieje", ex.getMessage());

		verify(timeOffLimitRepository, never()).save(any());
	}

	@Test
		// cases: 1
	void putAll_ShouldThrowApiException_WhenExistingLimitNotFound() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(2L);
		dto.setYear(2025);
		dto.setEmployeeId(1L);
		dto.setTypeId(10L);
		dto.setMaxHours(100);

		when(timeOffLimitRepository.findById(2L)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto)));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
		assertEquals("Nie znaleziono limitu o podanym id.", ex.getMessage());

		verify(timeOffLimitRepository, never()).save(any());
	}

	@Test
		// cases: 1
	void putAll_ShouldThrowApiException_WhenMaxHoursLessThanSumOfTimeOffs() {
		TimeOffLimitPutDto dto = new TimeOffLimitPutDto();
		dto.setId(1L);
		dto.setYear(2025);
		dto.setEmployeeId(1L);
		dto.setTypeId(10L);
		dto.setMaxHours(5); // less than sum of timeoffs hours

		TimeOffLimitEntity entity = new TimeOffLimitEntity();
		entity.setId(1L);
		entity.setMaxHours(10);

		List<TimeOffEntity> timeOffs = new ArrayList<>();
		TimeOffEntity to1 = new TimeOffEntity();
		to1.setHoursCount(3);
		TimeOffEntity to2 = new TimeOffEntity();
		to2.setHoursCount(4);
		timeOffs.add(to1);
		timeOffs.add(to2);

		entity.setTimeOffs(timeOffs);

		when(timeOffLimitRepository.findById(1L)).thenReturn(Optional.of(entity));

		ApiException ex = assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto)));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Nowa liczba godzin limitu nie wystarczy na zapisane urlopy", ex.getMessage());

		verify(timeOffLimitRepository, never()).save(any());
	}
}
