package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.employee.TestEmployeeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TestTimeOffLimitEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit.TestTimeOffLimitPutDtoBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type.TestTimeOffTypeEntityBuilder;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeOffLimitServiceUnitTests {
    @Mock
    private TimeOffLimitRepository timeOffLimitRepository;
    @Mock
    private TimeOffTypeService timeOffTypeService;
    @Mock
    private EmployeeService employeeService;
    @InjectMocks
    private TimeOffLimitService timeOffLimitService;

    @Test
    void getAllByYearAndEmployeeId_noLimitsDefined() {
        var types = List.of(
                new TestTimeOffTypeEntityBuilder().withName("type 1").withCompensationPercentage(0.5f).build(),
                new TestTimeOffTypeEntityBuilder().withName("type 2").withCompensationPercentage(0.2f).build()
        );
        var employee = new TestEmployeeEntityBuilder().build();
        doReturn(types).when(timeOffTypeService).getAll();
        doReturn(List.of()).when(timeOffLimitRepository).findAllByLeaveYearAndEmployeeId(2025, 1);
        doReturn(employee).when(employeeService).getById(anyLong());
        var result = timeOffLimitService.getAllByYearAndEmployeeId(2025, 1);
        assertEquals(types.size(), result.size());
        result.forEach(e -> {
            assertEquals(TimeOffLimitService.DEFAULT_MAX_HOURS, e.getMaxHours());
            assertEquals(employee, e.getEmployee());
            assertEquals(2025, e.getLeaveYear());
            assertEquals(0, e.getId());
        });
        types.forEach(t -> assertTrue(result.stream().anyMatch(r -> r.getTimeOffType().equals(t))));
    }

    @Test
    void getAllByYearAndEmployeeId_someLimitsDefined() {
        var employee = new TestEmployeeEntityBuilder().build();
        doReturn(employee).when(employeeService).getById(anyLong());
        var types = List.of(
                spy(new TestTimeOffTypeEntityBuilder().withName("type 1").withCompensationPercentage(0.5f).build()),
                new TestTimeOffTypeEntityBuilder().withName("type 2").withCompensationPercentage(0.2f).build()
        );
        doReturn(1L).when(types.getFirst()).getId();
        doReturn(types).when(timeOffTypeService).getAll();
        var limits = List.of(spy(new TestTimeOffLimitEntityBuilder(employee, types.getFirst()).withMaxHours(100).withLeaveYear(2025).build()));
        doReturn(1L).when(limits.getFirst()).getId();
        doReturn(limits).when(timeOffLimitRepository).findAllByLeaveYearAndEmployeeId(2025, 1);
        var result = timeOffLimitService.getAllByYearAndEmployeeId(2025, 1);
        assertEquals(types.size(), result.size());
        assertTrue(result.stream().anyMatch(r -> r.getTimeOffType().equals(types.getFirst())
                && r.getEmployee().equals(employee)
                && r.getId() == 1
                && r.getLeaveYear() == 2025
                && r.getMaxHours() == 100
        ));
        assertTrue(result.stream().anyMatch(r -> r.getTimeOffType().equals(types.getLast())
                && r.getEmployee().equals(employee)
                && r.getId() == 0
                && r.getLeaveYear() == 2025
                && r.getMaxHours() == TimeOffLimitService.DEFAULT_MAX_HOURS
        ));
    }

    @Test
    void getAllByYearAndEmployeeId_allLimitsDefined() {
        var employee = new TestEmployeeEntityBuilder().build();
        var types = List.of(
                new TestTimeOffTypeEntityBuilder().withName("type 1").withCompensationPercentage(0.5f).build(),
                new TestTimeOffTypeEntityBuilder().withName("type 2").withCompensationPercentage(0.2f).build()
        );
        doReturn(types).when(timeOffTypeService).getAll();
        var limits = List.of(
                spy(new TestTimeOffLimitEntityBuilder(employee, types.getFirst()).withMaxHours(100).withLeaveYear(2025).build()),
                spy(new TestTimeOffLimitEntityBuilder(employee, types.getLast()).withMaxHours(50).withLeaveYear(2025).build())
        );
        doReturn(1L).when(limits.getFirst()).getId();
        doReturn(2L).when(limits.getLast()).getId();
        doReturn(limits).when(timeOffLimitRepository).findAllByLeaveYearAndEmployeeId(2025, 1);
        var result = timeOffLimitService.getAllByYearAndEmployeeId(2025, 1);
        assertEquals(types.size(), result.size());
        assertTrue(result.stream().anyMatch(r -> r.getTimeOffType().equals(types.getFirst())
                && r.getEmployee().equals(employee)
                && r.getId() == 1
                && r.getLeaveYear() == 2025
                && r.getMaxHours() == 100
        ));
        assertTrue(result.stream().anyMatch(r -> r.getTimeOffType().equals(types.getLast())
                && r.getEmployee().equals(employee)
                && r.getId() == 2
                && r.getLeaveYear() == 2025
                && r.getMaxHours() == 50
        ));
    }

    @Test
    void getById_exists() {
        var timeOff = new TestTimeOffLimitEntityBuilder(null, null).build();
        doReturn(Optional.of(timeOff)).when(timeOffLimitRepository).findById(1L);
        assertEquals(timeOff, timeOffLimitService.getById(1L));
    }

    @Test
    void getById_doesNotExist() {
        doReturn(Optional.empty()).when(timeOffLimitRepository).findById(1L);
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class, () -> timeOffLimitService.getById(1L)).getStatus());
    }

    @Test
    void putAll_create_exists() {
        var dtos = List.of(new TestTimeOffLimitPutDtoBuilder().withId(0).build());
        doReturn(true).when(timeOffLimitRepository).existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(dtos.getFirst().getYear(), dtos.getFirst().getEmployeeId(), dtos.getFirst().getTypeId());
        assertEquals(HttpStatus.CONFLICT, assertThrows(ApiException.class, () -> timeOffLimitService.putAll(dtos)).getStatus());
        assertNoDeletions();
        assertNoSaves();
    }

    @Test
    void putAll_create_success() {
        var employee = spy(new TestEmployeeEntityBuilder().build());
        doReturn(1L).when(employee).getId();
        var type = spy(new TestTimeOffTypeEntityBuilder().build());
        doReturn(1L).when(type).getId();
        var dtos = List.of(new TestTimeOffLimitPutDtoBuilder().withId(0).withEmployeeId(employee.getId()).withTypeId(type.getId()).build());
        doReturn(false).when(timeOffLimitRepository).existsByLeaveYearAndEmployeeIdAndTimeOffTypeId(dtos.getFirst().getYear(), dtos.getFirst().getEmployeeId(), dtos.getFirst().getTypeId());
        doReturn(employee).when(employeeService).getById(dtos.getFirst().getEmployeeId());
        doReturn(type).when(timeOffTypeService).getById(dtos.getFirst().getTypeId());
        timeOffLimitService.putAll(dtos);
        assertNoDeletions();
        verify(timeOffLimitRepository).save(new TestTimeOffLimitEntityBuilder(employee, type).build());
    }

    @Test
    void putAll_update_doesNotExist() {
        var dtos = List.of(new TestTimeOffLimitPutDtoBuilder().withId(1).build());
        doReturn(Optional.empty()).when(timeOffLimitRepository).findById(1L);
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class, () -> timeOffLimitService.putAll(dtos)).getStatus());
        assertNoDeletions();
        assertNoSaves();
    }

    @Test
    void putAll_update_successChangesOnlyMaxHours() {
        var existingLimit = spy(new TestTimeOffLimitEntityBuilder(null, null).build());
        doReturn(Optional.of(existingLimit)).when(timeOffLimitRepository).findById(1L);
        var dtos = List.of(new TestTimeOffLimitPutDtoBuilder().withId(1L).withMaxHours(existingLimit.getMaxHours()+10).withTypeId(7L).withEmployeeId(10L).build());
        timeOffLimitService.putAll(dtos);
        assertNoDeletions();
        existingLimit.setMaxHours(dtos.getFirst().getMaxHours());
        verify(timeOffLimitRepository).save(existingLimit);
        verify(existingLimit, never()).setLeaveYear(anyInt());
        verify(existingLimit, never()).setTimeOffs(anyList());
        verify(existingLimit, never()).setEmployee(any());
        verify(existingLimit, never()).setTimeOffType(any());
    }

    @Test
    void putAll_update_newLimitTooSmall() {
        var mockedTimeOff = mock(TimeOffEntity.class);
        doReturn(2).when(mockedTimeOff).getHoursCount();
        var existingLimit = mock(TimeOffLimitEntity.class);
        doReturn(Optional.of(existingLimit)).when(timeOffLimitRepository).findById(1L);
        doReturn(List.of(mockedTimeOff)).when(existingLimit).getTimeOffs();
        var dto = mock(TimeOffLimitPutDto.class);
        doReturn(1L).when(dto).getId();
        doReturn(1).when(dto).getMaxHours();
        var dtos = List.of(dto);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ApiException.class, () -> timeOffLimitService.putAll(dtos)).getStatus());
        assertNoDeletions();
        assertNoSaves();
    }
    @Test
    void putAll_createAndUpdate_success() {
        var employee = mock(EmployeeEntity.class);
        var type = mock(TimeOffTypeEntity.class);
        var existingLimit = new TestTimeOffLimitEntityBuilder(employee, type).build();
        doReturn(existingLimit).when(timeOffLimitRepository).findById(1L);
        var dtos = List.of(
                new TestTimeOffLimitPutDtoBuilder().withId(1L).build()
        );
        timeOffLimitService.putAll(dtos);
        assertNoDeletions();
        verify(timeOffLimitRepository).save(existingLimit);
    }

    private void assertNoDeletions() {
        verify(timeOffLimitRepository, never()).delete(any());
        verify(timeOffLimitRepository, never()).deleteAll();
        verify(timeOffLimitRepository, never()).deleteAll(anyList());
    }

    private void assertNoSaves() {
        verify(timeOffLimitRepository, never()).save(any());
        verify(timeOffLimitRepository, never()).saveAll(anyList());
    }
}
