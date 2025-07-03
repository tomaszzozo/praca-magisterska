// Testy jednostkowe TimeOffTypeService
package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
// ai tag: unit
class TimeOffTypeServiceUnitTest {

	private TimeOffTypeRepository timeOffTypeRepository;
	private TimeOffTypeService timeOffTypeService;

	@BeforeEach
	void setUp() {
		timeOffTypeRepository = mock(TimeOffTypeRepository.class);
		timeOffTypeService = new TimeOffTypeService(timeOffTypeRepository);
	}

	@Test
	void getAll_ShouldReturnList() {
		List<TimeOffTypeEntity> list = List.of(new TimeOffTypeEntity());
		when(timeOffTypeRepository.findAll()).thenReturn(list);

		List<TimeOffTypeEntity> result = timeOffTypeService.getAll();

		assertEquals(list, result);
	}

	@Test
	void getById_ShouldReturnEntity_WhenFound() {
		TimeOffTypeEntity entity = new TimeOffTypeEntity();
		entity.setId(1L);
		when(timeOffTypeRepository.findById(1L)).thenReturn(Optional.of(entity));

		TimeOffTypeEntity result = timeOffTypeService.getById(1L);

		assertEquals(entity, result);
	}

	@Test
	void getById_ShouldThrowApiException_WhenNotFound() {
		when(timeOffTypeRepository.findById(1L)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> timeOffTypeService.getById(1L));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
		assertEquals("Typ urlopu z podanym id nie istnieje.", ex.getMessage());
	}

	@Test
	void putAll_ShouldDeleteUnusedEntitiesAndSaveAllDtos() {
		TimeOffTypeEntity existing1 = new TimeOffTypeEntity();
		existing1.setId(1L);
		existing1.setName("existing1");
		existing1.setTimeOffs(new ArrayList<>()); // no usage

		TimeOffTypeEntity existing2 = new TimeOffTypeEntity();
		existing2.setId(2L);
		existing2.setName("existing2");
		existing2.setTimeOffs(new ArrayList<>()); // no usage

		List<TimeOffTypeEntity> allExisting = List.of(existing1, existing2);

		TimeOffTypePutDto dto1 = new TimeOffTypePutDto();
		dto1.setId(1L);
		dto1.setName("updated1");

		TimeOffTypePutDto dtoNew = new TimeOffTypePutDto();
		dtoNew.setId(0);
		dtoNew.setName("newType");

		List<TimeOffTypePutDto> dtos = List.of(dto1, dtoNew);

		when(timeOffTypeRepository.findAll()).thenReturn(allExisting);
		when(timeOffTypeRepository.existsByName("newType")).thenReturn(false);
		when(timeOffTypeRepository.existsByName("updated1")).thenReturn(false);
		when(timeOffTypeRepository.findById(1L)).thenReturn(Optional.of(existing1));

		timeOffTypeService.putAll(dtos);

		ArgumentCaptor<List<TimeOffTypeEntity>> deleteCaptor = ArgumentCaptor.forClass(List.class); // INACCURACY: Unchecked assignment: 'org.mockito.ArgumentCaptor' to 'org.mockito.ArgumentCaptor<java.util.List<com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity>>'
		verify(timeOffTypeRepository).deleteAll(deleteCaptor.capture());
		List<TimeOffTypeEntity> deleted = deleteCaptor.getValue();
		assertEquals(1, deleted.size());
		assertEquals(existing2, deleted.get(0));

		ArgumentCaptor<TimeOffTypeEntity> saveCaptor = ArgumentCaptor.forClass(TimeOffTypeEntity.class);
		verify(timeOffTypeRepository, times(2)).save(saveCaptor.capture());
		List<TimeOffTypeEntity> saved = saveCaptor.getAllValues();

		assertTrue(saved.stream().anyMatch(e -> e.getName().equals("updated1")));
		assertTrue(saved.stream().anyMatch(e -> e.getName().equals("newType")));
	}

	@Test
	void putAll_ShouldThrowApiException_WhenUsedEntityWouldBeDeleted() {
		TimeOffTypeEntity used = new TimeOffTypeEntity();
		used.setId(1L);
		used.setTimeOffs(List.of(new TimeOffEntity())); // MISTAKE: 'used.setTimeOffs(List.of(new Object()));' no instance(s) of type variable(s) exist so that Object conforms to TimeOffEntity inference variable E has incompatible bounds: equality constraints: TimeOffEntity lower bounds: Object

		TimeOffTypePutDto dto = new TimeOffTypePutDto(); // INACCURACY: write-only object
		dto.setId(1L);
		dto.setName("sameName");

		when(timeOffTypeRepository.findAll()).thenReturn(List.of(used));
		List<TimeOffTypePutDto> dtos = List.of(); // empty list, so entity would be deleted

		ApiException ex = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(dtos));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Typ urlopu jest używany - nie można usunąć", ex.getMessage());

		verify(timeOffTypeRepository, never()).deleteAll(any());
	}

	@Test
	void putAll_ShouldThrowApiException_WhenNewNameAlreadyExists() {
		TimeOffTypePutDto newDto = new TimeOffTypePutDto();
		newDto.setId(0);
		newDto.setName("duplicateName");

		when(timeOffTypeRepository.findAll()).thenReturn(List.of());
		when(timeOffTypeRepository.existsByName("duplicateName")).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(newDto)));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Podana nazwa jest już zajęta", ex.getMessage());

		verify(timeOffTypeRepository, never()).save(any());
	}

	@Test
	void putAll_ShouldThrowApiException_WhenExistingNameChangeToDuplicate() {
		TimeOffTypeEntity existing = new TimeOffTypeEntity();
		existing.setId(1L);
		existing.setName("oldName");
		existing.setTimeOffs(new ArrayList<>());

		TimeOffTypePutDto dto = new TimeOffTypePutDto();
		dto.setId(1L);
		dto.setName("duplicateName");

		when(timeOffTypeRepository.findAll()).thenReturn(List.of(existing));
		when(timeOffTypeRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(timeOffTypeRepository.existsByName("duplicateName")).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(dto)));
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		assertEquals("Podana nazwa jest już zajęta", ex.getMessage());

		verify(timeOffTypeRepository, never()).save(any());
	}
}
