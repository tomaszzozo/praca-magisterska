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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class TimeOffServiceTest {

	private TimeOffRepository repository;
	private TimeOffLimitService limitService;
	private TimeOffTypeService typeService;
	private EmployeeService employeeService;
	private TimeOffService service;

	@BeforeEach
	void setup() {
		repository = mock(TimeOffRepository.class);
		limitService = mock(TimeOffLimitService.class);
		typeService = mock(TimeOffTypeService.class);
		employeeService = mock(EmployeeService.class);
		service = new TimeOffService(repository, limitService, typeService, employeeService);
	}

	@Test
		// cases: 1
	void getAllByYearAndEmployeeId_shouldReturnList() {
		when(repository.findAllByYearAndEmployeeId(1L, 2024)).thenReturn(List.of(new TimeOffEntity()));

		var result = service.getAllByYearAndEmployeeId(2024, 1L);

		assertThat(result).hasSize(1);
	}

	@Test
		// cases: 1
	void post_shouldSave_whenValid() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setEmployeeId(1L);
		dto.setTypeId(2L);
		dto.setYearlyLimitId(3L);
		dto.setFirstDay(LocalDate.of(2024, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2024, 6, 5));
		dto.setHoursCount(40);

		when(repository.findAllByYearAndEmployeeId(1L, 2024)).thenReturn(List.of());
		when(limitService.getById(3L)).thenReturn(new TimeOffLimitEntity() {{
			setMaxHours(100);
		}});
		when(typeService.getById(2L)).thenReturn(new TimeOffTypeEntity());
		when(employeeService.getById(1L)).thenReturn(new EmployeeEntity());

		service.post(dto);

		verify(repository).save(any(TimeOffEntity.class));
	}

	@Test
		// cases: 1
	void post_shouldThrow_whenOverlapping() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setEmployeeId(1L);
		dto.setFirstDay(LocalDate.of(2024, 6, 5));
		dto.setLastDayInclusive(LocalDate.of(2024, 6, 10));

		TimeOffEntity existing = new TimeOffEntity();
		existing.setFirstDay(LocalDate.of(2024, 6, 3));
		existing.setLastDayInclusive(LocalDate.of(2024, 6, 6));

		when(repository.findAllByYearAndEmployeeId(1L, 2024)).thenReturn(List.of(existing));

		assertThatThrownBy(() -> service.post(dto)).isInstanceOf(ApiException.class);
	}

	@Test
		// cases: 1
	void post_shouldThrow_whenOverLimit() {
		TimeOffPostDto dto = new TimeOffPostDto();
		dto.setEmployeeId(1L);
		dto.setTypeId(2L);
		dto.setYearlyLimitId(3L);
		dto.setFirstDay(LocalDate.of(2024, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2024, 6, 2));
		dto.setHoursCount(10);

		TimeOffEntity existing = new TimeOffEntity();
		TimeOffTypeEntity type = new TimeOffTypeEntity();
		type.setId(2L);
		existing.setTimeOffType(type);
		existing.setHoursCount(95);
		// MISTAKE: required field not set
		existing.setFirstDay(dto.getFirstDay().minusMonths(1));
		// MISTAKE: required field not set
		existing.setLastDayInclusive(dto.getLastDayInclusive().minusMonths(1));

		when(repository.findAllByYearAndEmployeeId(1L, 2024)).thenReturn(List.of(existing));
		when(limitService.getById(3L)).thenReturn(new TimeOffLimitEntity() {{
			setMaxHours(100);
		}});

		assertThatThrownBy(() -> service.post(dto)).isInstanceOf(ApiException.class);
	}

	@Test
		// cases: 1
	void put_shouldSave_whenValid() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2024, 6, 1));
		dto.setLastDayInclusive(LocalDate.of(2024, 6, 3));
		dto.setEmployeeId(1L);
		dto.setTypeId(2L);
		dto.setYearlyLimitId(3L);
		dto.setHoursCount(8);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setId(10L);
		existing.setFirstDay(LocalDate.of(2024, 6, 1));

		when(repository.findById(10L)).thenReturn(Optional.of(existing));
		when(repository.findAllByYearAndEmployeeId(1L, 2024)).thenReturn(List.of());

		when(limitService.getById(3L)).thenReturn(new TimeOffLimitEntity() {{
			setMaxHours(40);
		}});
		when(typeService.getById(2L)).thenReturn(new TimeOffTypeEntity());
		when(employeeService.getById(1L)).thenReturn(new EmployeeEntity());

		service.put(10L, dto);

		verify(repository).save(existing);
	}

	@Test
		// cases: 1
	void put_shouldThrow_whenChangingYear() {
		TimeOffPutDto dto = new TimeOffPutDto();
		dto.setFirstDay(LocalDate.of(2025, 1, 1));
		dto.setEmployeeId(1L);

		TimeOffEntity existing = new TimeOffEntity();
		existing.setFirstDay(LocalDate.of(2024, 12, 31));

		when(repository.findById(1L)).thenReturn(Optional.of(existing));

		assertThatThrownBy(() -> service.put(1L, dto)).isInstanceOf(ApiException.class);
	}

	@Test
		// cases: 1
	void delete_shouldRemove_whenExists() {
		TimeOffEntity entity = new TimeOffEntity();
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		service.delete(1L);

		verify(repository).delete(entity);
	}

	@Test
		// cases: 1
	void delete_shouldThrow_whenNotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(ApiException.class);
	}
}
