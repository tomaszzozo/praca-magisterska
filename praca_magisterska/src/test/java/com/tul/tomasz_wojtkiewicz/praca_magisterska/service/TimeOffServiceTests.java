package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootTest
class TimeOffServiceTests {
    @Autowired
    private TimeOffRepository timeOffRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffLimitRepository timeOffLimitRepository;
    @Autowired
    private TimeOffService timeOffService;

    @AfterEach
    void afterEach() {
        timeOffRepository.deleteAll();
        timeOffLimitRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void getAllByYearAndEmployeeId() {
        var employees = ValidDataProvider.getEmployees().subList(0, 4);
        employeeRepository.saveAll(employees);
        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);
        var limits = ValidDataProvider.getTimeOffLimits(employees, types);
        timeOffLimitRepository.saveAll(limits);
        var timeOffs = ValidDataProvider.getTimeOffs(limits);
        timeOffRepository.saveAll(timeOffs);

        var years = timeOffs.stream().map(t -> t.getFirstDay().getYear()).collect(Collectors.toSet());
        for (var e : employees) {
            for (var y : years) {
                var result = timeOffService.getAllByYearAndEmployeeId(y, e.getId()).stream().map(TimeOffEntity::getId).collect(Collectors.toSet());
                var expected = timeOffs.stream().filter(t -> t.getEmployee().getId() == e.getId() && t.getFirstDay().getYear() == y).map(TimeOffEntity::getId).collect(Collectors.toSet());
                Assertions.assertEquals(expected, result);
            }
        }
    }

    @Test
    void getAllByYearAndEmployeeId_invalidId() {
        List.of(Long.MIN_VALUE, -54321L, -1L, 0L).forEach(id -> Assertions.assertThrows(ValidationException.class, () -> timeOffService.getAllByYearAndEmployeeId(2025, id)));
        Assertions.assertTrue(timeOffService.getAllByYearAndEmployeeId(2025, 1).isEmpty());
    }

    @Test
    void getAllByYearAndEmployeeId_invalidYear() {
        List.of(Integer.MIN_VALUE, -1000, -1, 0, 1, 100, 2000, 2019, 2101, 2222222, Integer.MAX_VALUE).forEach(y -> Assertions.assertThrows(ValidationException.class, () -> timeOffService.getAllByYearAndEmployeeId(y, 1)));
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        for (var y = new AtomicInteger(2020); y.get() <= 2100; y.set(y.incrementAndGet())) {
            Assertions.assertDoesNotThrow(() -> timeOffService.getAllByYearAndEmployeeId(y.get(), employee.getId()));
        }
    }

    @Test
    void post_invalidHoursCount() {
        var ids = createTestObjects();
        List.of(Integer.MIN_VALUE, -12343, -1, 0).forEach(h -> {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setHoursCount(h);
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        });
        var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
        dto.setHoursCount(1);
        Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
    }

    @Test
    void post_firstDayAfterLastDay() {
        var ids = createTestObjects();

        List.of(1, 5, 10, 30).forEach(d -> {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setLastDayInclusive(dto.getFirstDay().minusDays(d));
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        });

        var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
        Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
    }

    @Test
    void post_firstAndLastDayYearRange() {
        var ids = createTestObjects();
        List.of(LocalDate.MIN.getYear(), -123456789, -12345678, -1234567, -123456, -12345, -1234, -123, -1, 0, 1, 123, 1234, 2000, 2019, 2101, 2200, 12345, 123456, 1234567, 12345678, 123456789, LocalDate.MAX.getYear()).forEach(y -> {
            {
                var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
                dto.setFirstDay(LocalDate.of(y, 1, 1));
                Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
            }
            {
                var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
                dto.setLastDayInclusive(LocalDate.of(y, 1, 1));
                Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
            }
        });
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setFirstDay(LocalDate.of(2020, 1, 1));
            dto.setLastDayInclusive(dto.getFirstDay());
            Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
        }
        timeOffRepository.deleteAll();
        var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
        dto.setFirstDay(LocalDate.of(2100, 12, 31));
        dto.setLastDayInclusive(dto.getFirstDay());
        Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
    }

    @Test
    void post_firstAndLastDayMonthAndYearDoNotMatch() {
        var ids = createTestObjects();

        for (int m = 2; m <= 12; m++) {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setFirstDay(LocalDate.of(2025, 1, 1));
            dto.setLastDayInclusive(LocalDate.of(2025, m, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        }
        for (int y = 2021; y < 2100; y++) {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setFirstDay(LocalDate.of(2020, 1, 1));
            dto.setLastDayInclusive(LocalDate.of(y, 1, 1));
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        }
    }

    @Test
    void post_hoursCountThanHoursInTimeOffPeriod() {
        var ids = createTestObjects();
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setHoursCount((int)dto.getLastDayInclusive().until(dto.getFirstDay(), ChronoUnit.DAYS)*24+24+1);
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        }
        var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
        dto.setHoursCount((int)dto.getLastDayInclusive().until(dto.getFirstDay(), ChronoUnit.DAYS)*24+24);
        Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
    }

    @Test
    void post_nullFields() {
        var ids = createTestObjects();
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setComment(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        }
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setFirstDay(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        }
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setLastDayInclusive(null);
            Assertions.assertThrows(ValidationException.class, () -> timeOffService.post(dto));
        }
    }

    @Test
    void post_timeOffCollision() {
        var ids = createTestObjects();
        var timeOff = DefaultTestObjects.getTimeOffEntity(timeOffLimitRepository.findById(ids.limitId).orElseThrow());
        timeOff.setFirstDay(LocalDate.of(2025, 3, 10));
        timeOff.setLastDayInclusive(timeOff.getFirstDay().plusDays(10));
        timeOffRepository.save(timeOff);
        List.of(
                Pair.of(timeOff.getFirstDay(), timeOff.getLastDayInclusive()),
                Pair.of(timeOff.getFirstDay().plusDays(1), timeOff.getLastDayInclusive()),
                Pair.of(timeOff.getFirstDay(), timeOff.getLastDayInclusive().minusDays(1)),
                Pair.of(timeOff.getFirstDay().minusDays(1), timeOff.getLastDayInclusive()),
                Pair.of(timeOff.getFirstDay(), timeOff.getLastDayInclusive().plusDays(1)),
                Pair.of(timeOff.getFirstDay().minusDays(1), timeOff.getLastDayInclusive().minusDays(1)),
                Pair.of(timeOff.getFirstDay().plusDays(1), timeOff.getLastDayInclusive().plusDays(1)),
                Pair.of(timeOff.getLastDayInclusive(), timeOff.getLastDayInclusive().plusDays(2)),
                Pair.of(timeOff.getFirstDay().minusDays(2), timeOff.getFirstDay())
        ).forEach(pair -> {
            var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
            dto.setFirstDay(pair.getFirst());
            dto.setLastDayInclusive(pair.getSecond());
            Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffService.post(dto)).getStatus());
        });
        var dto = DefaultTestObjects.getTimeOffPostDto(ids.typeId, ids.employeeId, ids.limitId);
        dto.setFirstDay(timeOff.getLastDayInclusive().plusDays(1));
        dto.setLastDayInclusive(dto.getFirstDay());
        Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
    }

    @Test
    void post_timeOffWillOverbookLimit() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var limit = DefaultTestObjects.getLimitEntity(type, employee);
        limit.setMaxHours(0);
        timeOffLimitRepository.save(limit);
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(type.getId(), employee.getId(), limit.getId());
            dto.setHoursCount(1);
            Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffService.post(dto)).getStatus());
        }
        limit.setMaxHours(8);
        timeOffLimitRepository.save(limit);
        {
            var dto = DefaultTestObjects.getTimeOffPostDto(type.getId(), employee.getId(), limit.getId());
            dto.setHoursCount(8);
            Assertions.assertDoesNotThrow(() -> timeOffService.post(dto));
            dto.setFirstDay(dto.getFirstDay().plusDays(1));
            dto.setLastDayInclusive(dto.getFirstDay());
            dto.setHoursCount(1);
            Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffService.post(dto)).getStatus());
        }
    }

    private TestIds createTestObjects() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var limit = DefaultTestObjects.getLimitEntity(type, employee);
        timeOffLimitRepository.save(limit);
        return new TestIds(employee.getId(), type.getId(), limit.getId());
    }

    private record TestIds(long employeeId, long typeId, long limitId) {
    }
}
