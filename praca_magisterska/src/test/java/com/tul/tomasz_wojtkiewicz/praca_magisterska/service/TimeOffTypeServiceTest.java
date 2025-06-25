package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
// at tag: unit
class TimeOffTypeServiceTest {

	private TimeOffTypeRepository repository;
	private TimeOffTypeService service;

	@BeforeEach
	void setup() {
		repository = mock(TimeOffTypeRepository.class);
		service = new TimeOffTypeService(repository);
	}

	@Test
		// cases: 1
	void getById_shouldReturnEntity_whenFound() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		var result = service.getById(1L);

		assertThat(result).isEqualTo(entity);
	}

	@Test
		// cases: 1
	void getById_shouldThrow_whenNotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(1L))
			.isInstanceOf(ApiException.class);
	}

	@Test
		// cases: 1
	void putAll_shouldCreateNew_whenIdIsZero() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(0);
		dto.setName("Nowy");

		when(repository.findAll()).thenReturn(List.of());
		when(repository.existsByName("Nowy")).thenReturn(false);

		service.putAll(List.of(dto));

		ArgumentCaptor<TimeOffTypeEntity> captor = ArgumentCaptor.forClass(TimeOffTypeEntity.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getName()).isEqualTo("Nowy");
	}

	@Test
		// cases: 1
	void putAll_shouldThrow_whenNameConflictOnCreate() {
		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(0);
		dto.setName("Zajęta");

		when(repository.findAll()).thenReturn(List.of());
		when(repository.existsByName("Zajęta")).thenReturn(true);

		assertThatThrownBy(() -> service.putAll(List.of(dto)))
			.isInstanceOf(ApiException.class);
	}

	@Test
		// cases: 1
	void putAll_shouldUpdate_whenExistsAndNameIsUnique() {
		TimeOffTypeEntity existing = new TimeOffTypeEntity();
		existing.setId(5L);
		existing.setName("Old");

		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(5);
		dto.setName("New");

		when(repository.findAll()).thenReturn(List.of(existing));
		when(repository.findById(5L)).thenReturn(Optional.of(existing));
		when(repository.existsByName("New")).thenReturn(false);

		service.putAll(List.of(dto));

		verify(repository).save(any(TimeOffTypeEntity.class));
	}

	@Test
		// cases: 1
	void putAll_shouldDeleteUnusedTypes() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setId(10L);
		entity.setTimeOffs(Collections.emptyList());

		when(repository.findAll()).thenReturn(List.of(entity));

		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1);
		dto.setName("A");

		when(repository.findById(1L)).thenReturn(Optional.of(new TimeOffTypeEntity()));
		when(repository.existsByName("A")).thenReturn(false);

		service.putAll(List.of(dto));

		verify(repository).deleteAll(List.of(entity));
	}

	@Test
		// cases: 1
	void putAll_shouldThrow_whenDeletingTypeInUse() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setId(10L);
		entity.setTimeOffs(List.of(new com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity()));

		when(repository.findAll()).thenReturn(List.of(entity));

		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1);
		dto.setName("X");

		assertThatThrownBy(() -> service.putAll(List.of(dto)))
			.isInstanceOf(ApiException.class);
	}
}
