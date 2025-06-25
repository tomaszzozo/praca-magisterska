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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class TimeOffLimitServiceTest {

	private TimeOffLimitRepository repository;
	private TimeOffTypeService typeService;
	private EmployeeService employeeService;
	private TimeOffLimitService service;

	@BeforeEach
	void setup() {
		repository = mock(TimeOffLimitRepository.class);
		typeService = mock(TimeOffTypeService.class);
		employeeService = mock(EmployeeService.class);
		service = new TimeOffLimitService(repository, typeService, employeeService);
	}

	@Test
	void getById_shouldReturnEntity_whenExists() {
		var entity = new TimeOffLimitEntity();
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		var result = service.getById(1L);

		assertThat(result).isSameAs(entity);
	}

	@Test
	void getById_shouldThrow_whenNotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(1L))
			.isInstanceOf(ApiException.class);
	}


	@Test
	void putAll_shouldSaveNewEntity_whenIdIsZero() {
		var dto = new TimeOffLimitPutDto();
		dto.setId(0L); // MISTAKE: type int instead of long
		dto.setYear(2025);
		dto.setEmployeeId(1L);
		dto.setTypeId(2L);
		dto.setMaxHours(100);

		when(repository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(2025, 1L, 2L)).thenReturn(false);
		when(employeeService.getById(1L)).thenReturn(new EmployeeEntity());
		when(typeService.getById(2L)).thenReturn(new TimeOffTypeEntity());

		service.putAll(List.of(dto));

		verify(repository).save(any(TimeOffLimitEntity.class));
	}


	@Test
	void putAll_shouldThrow_whenLimitAlreadyExists() {
		var dto = new TimeOffLimitPutDto();
		dto.setId(0L); // MISTAKE: type int instead of long
		dto.setYear(2025);
		dto.setEmployeeId(1L);
		dto.setTypeId(2L);

		when(repository.existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(2025, 1L, 2L)).thenReturn(true);

		assertThatThrownBy(() -> service.putAll(List.of(dto)))
			.isInstanceOf(ApiException.class);
	}


	@Test
	void putAll_shouldUpdateExisting_whenValid() {
		var dto = new TimeOffLimitPutDto();
		dto.setId(10L);
		dto.setMaxHours(50);

		var entity = new TimeOffLimitEntity();
		entity.setTimeOffs(List.of());

		when(repository.findById(10L)).thenReturn(Optional.of(entity));

		service.putAll(List.of(dto));

		verify(repository).save(entity);
	}

	@Test
	void putAll_shouldThrow_whenUsedHoursExceedNewLimit() {
		var dto = new TimeOffLimitPutDto();
		dto.setId(10L);
		dto.setMaxHours(3);

		var timeOff = new TimeOffEntity();
		timeOff.setHoursCount(5);
		var entity = new TimeOffLimitEntity();
		entity.setTimeOffs(List.of(timeOff));

		when(repository.findById(10L)).thenReturn(Optional.of(entity));

		assertThatThrownBy(() -> service.putAll(List.of(dto)))
			.isInstanceOf(ApiException.class);
	}

	@Test
	void getAllByYearAndEmployeeId_shouldReturnCompleteList() {
		var employee = new EmployeeEntity();
		employee.setId(1L);
		var type1 = new TimeOffTypeEntity();
		type1.setId(100L);
		var type2 = new TimeOffTypeEntity();
		type2.setId(200L);

		var limit = new TimeOffLimitEntity();
		limit.setTimeOffType(type1);
		limit.setLeaveYear(2025);

		when(employeeService.getById(1L)).thenReturn(employee);
		when(typeService.getAll()).thenReturn(List.of(type1, type2));
		when(repository.findAllByLeaveYearAndEmployeeId(2025, 1L)).thenReturn(List.of(limit));

		var result = service.getAllByYearAndEmployeeId(2025, 1L);

		assertThat(result).hasSize(2);
		assertThat(result).extracting("timeOffType.id").containsExactlyInAnyOrder(100L, 200L);
	}
}
