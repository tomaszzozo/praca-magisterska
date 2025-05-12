package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TestTimeOffTypeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_type.TestTimeOffTypePutDtoBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeOffTypeServiceUnitTests {
    @Mock
    private TimeOffTypeRepository timeOffTypeRepository;
    @InjectMocks
    private TimeOffTypeService timeOffTypeService;

    @Test
    void getAll() {
        var type = new TestTimeOffTypeEntityBuilder().build();
        when(timeOffTypeRepository.findAll()).thenReturn(List.of(type));
        var result = assertDoesNotThrow(() -> timeOffTypeService.getAll());
        verify(timeOffTypeRepository).findAll();
        assertEquals(1, result.size());
        assertEquals(type, result.getFirst());
    }

    @Test
    void getById_typeExists() {
        var type = new TestTimeOffTypeEntityBuilder().build();
        when(timeOffTypeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(type));
        var result = timeOffTypeService.getById(1);
        assertNotNull(result);
        assertEquals(type, result);
        verify(timeOffTypeRepository).findById(1L);
    }

    @Test
    void getById_employeeDoesNotExists() {
        when(timeOffTypeRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class, () -> timeOffTypeService.getById(1)).getStatus());
        verify(timeOffTypeRepository).findById(1L);
    }

    @Test
    void putAll_deleteUnusedType() {
        var existingEntity = new TestTimeOffTypeEntityBuilder().withTimeOffs(List.of()).build();
        when(timeOffTypeRepository.findAll()).thenReturn(List.of(existingEntity));
        timeOffTypeService.putAll(List.of());
        verify(timeOffTypeRepository).deleteAll(List.of(existingEntity));
    }

    @Test
    void putAll_deleteUsedType() {
        List<TimeOffEntity> spyList = spy(List.of());
        doReturn(false).when(spyList).isEmpty();
        var existingEntity = spy(new TestTimeOffTypeEntityBuilder().withTimeOffs(spyList).build());
        when(timeOffTypeRepository.findAll()).thenReturn(List.of(existingEntity));
        List<TimeOffTypePutDto> emptyList = List.of();
        assertEquals(HttpStatus.CONFLICT, assertThrows(ApiException.class, () -> timeOffTypeService.putAll(emptyList)).getStatus());
        verify(timeOffTypeRepository, never()).deleteAll(anyList());
        verify(timeOffTypeRepository, never()).delete(any());
    }

    @Test
    void putAll_createNewType() {
        timeOffTypeService = spy(timeOffTypeService);
        var dto = new TestTimeOffTypePutDtoBuilder().build();
        when(timeOffTypeRepository.findAll()).thenReturn(List.of());
        when(timeOffTypeRepository.existsByName(anyString())).thenReturn(false);
        timeOffTypeService.putAll(List.of(dto));
        verify(timeOffTypeRepository).existsByName(dto.getName());
        verify(timeOffTypeRepository).save(new TestTimeOffTypeEntityBuilder().build());
        verify(timeOffTypeService, never()).getById(anyLong());
    }

    @Test
    void putAll_newTypeNameTaken() {
        timeOffTypeService = spy(timeOffTypeService);
        var dto = new TestTimeOffTypePutDtoBuilder().build();
        when(timeOffTypeRepository.findAll()).thenReturn(List.of());
        when(timeOffTypeRepository.existsByName(anyString())).thenReturn(true);
        var dtos = List.of(dto);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ApiException.class, () -> timeOffTypeService.putAll(dtos)).getStatus());
        verify(timeOffTypeRepository, never()).save(any());
        verify(timeOffTypeRepository, never()).saveAll(any());
        verify(timeOffTypeService, never()).getById(anyLong());
    }

    @Test
    void putAll_updateType() {
        var entityToUpdate = spy(new TestTimeOffTypeEntityBuilder().build());
        when(entityToUpdate.getId()).thenReturn(1L);
        timeOffTypeService = spy(timeOffTypeService);
        doReturn(entityToUpdate).when(timeOffTypeService).getById(1);
        var dto = new TestTimeOffTypePutDtoBuilder().withName(entityToUpdate.getName()+" update").withId(entityToUpdate.getId()).build();
        when(timeOffTypeRepository.findAll()).thenReturn(List.of(entityToUpdate));
        when(timeOffTypeRepository.existsByName(anyString())).thenReturn(false);
        assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of(dto)));
        entityToUpdate.setName(dto.getName());
        verify(timeOffTypeRepository).save(entityToUpdate);
        verify(timeOffTypeService).getById(entityToUpdate.getId());
    }

    @Test
    void putAll_updateTypeNameTaken() {
        timeOffTypeService = spy(timeOffTypeService);
        var existingEntity = new TestTimeOffTypeEntityBuilder().build();
        doReturn(existingEntity).when(timeOffTypeService).getById(1);
        var dto = new TestTimeOffTypePutDtoBuilder().withName(existingEntity.getName()+" update").withId(1L).build();
        when(timeOffTypeRepository.existsByName(anyString())).thenReturn(true);
        var dtos = List.of(dto);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ApiException.class, () -> timeOffTypeService.putAll(dtos)).getStatus());
        verify(timeOffTypeRepository, never()).save(any());
        verify(timeOffTypeRepository, never()).saveAll(anyList());
    }

    @Test
    void putAll_updateCreateAndDeleteType() {
        var entityToDelete = spy(new TestTimeOffTypeEntityBuilder().withTimeOffs(List.of()).build());
        when(entityToDelete.getId()).thenReturn(1L);
        var entityToUpdate = spy(new TestTimeOffTypeEntityBuilder().withName("update me").withTimeOffs(List.of()).build());
        when(entityToUpdate.getId()).thenReturn(2L);
        when(timeOffTypeRepository.findAll()).thenReturn(List.of(entityToDelete, entityToUpdate));
        when(timeOffTypeRepository.existsByName(anyString())).thenReturn(false);
        timeOffTypeService = spy(timeOffTypeService);
        doReturn(entityToUpdate).when(timeOffTypeService).getById(2);
        var dtos = List.of(
                new TestTimeOffTypePutDtoBuilder().withName("updated").withId(entityToUpdate.getId()).build(),
                new TestTimeOffTypePutDtoBuilder().withName("new type").build()
        );
        assertDoesNotThrow(() -> timeOffTypeService.putAll(dtos));
        entityToUpdate.setName(dtos.getFirst().getName());
        verify(timeOffTypeRepository).deleteAll(List.of(entityToDelete));
        verify(timeOffTypeRepository).save(entityToUpdate);
        verify(timeOffTypeRepository).save(new TestTimeOffTypeEntityBuilder().withName(dtos.getLast().getName()).withCompensationPercentage(dtos.getLast().getCompensationPercentage()).build());
    }
}
