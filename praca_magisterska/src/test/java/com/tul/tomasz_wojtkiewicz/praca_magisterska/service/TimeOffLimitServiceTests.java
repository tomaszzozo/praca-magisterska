package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class TimeOffLimitServiceTests {
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffLimitRepository timeOffLimitRepository;
    @Autowired
    private TimeOffLimitService timeOffLimitService;

    @AfterEach
    void afterEach() {
        timeOffLimitRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void getAllByYear() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var types = ValidDataProvider.getTimeOffTypes();
        timeOffTypeRepository.saveAll(types);
        var leaveYear = LocalDate.now().getYear();

        {
            var result = timeOffLimitService.getAllByYearAndEmployeeId(leaveYear, employee.getId());
            Assertions.assertEquals(types.size(), result.size());

            types.forEach(type -> {
                var limit = result.stream().filter(l -> l.getTimeOffType().equals(type)).findAny();
                Assertions.assertTrue(limit.isPresent());
                Assertions.assertEquals(0, limit.get().getMaxHours());
                Assertions.assertEquals(employee, limit.get().getEmployee());
                Assertions.assertEquals(leaveYear, limit.get().getLeaveYear());
            });
        }

        var createdLimit = DefaultTestObjects.getLimitEntity(types.getFirst(), employee);
        createdLimit.setMaxHours(120);
        createdLimit.setLeaveYear(leaveYear);
        timeOffLimitRepository.save(createdLimit);

        {
            var result = timeOffLimitService.getAllByYearAndEmployeeId(leaveYear, employee.getId());
            Assertions.assertEquals(types.size(), result.size());
            Assertions.assertTrue(result.contains(createdLimit));

            types.stream().filter(type -> !type.equals(createdLimit.getTimeOffType())).forEach(type -> {
                var limit = result.stream().filter(l -> l.getTimeOffType().equals(type)).findAny();
                Assertions.assertTrue(limit.isPresent());
                Assertions.assertEquals(0, limit.get().getMaxHours());
                Assertions.assertEquals(employee, limit.get().getEmployee());
                Assertions.assertEquals(leaveYear, limit.get().getLeaveYear());
            });

            types.stream().filter(type -> !type.equals(createdLimit.getTimeOffType())).forEach(type -> {
                var newLimit = DefaultTestObjects.getLimitEntity(types.getFirst(), employee);
                newLimit.setMaxHours(120);
                newLimit.setLeaveYear(leaveYear);
                newLimit.setTimeOffType(type);
                timeOffLimitRepository.save(newLimit);
            });
        }

        var result = timeOffLimitService.getAllByYearAndEmployeeId(leaveYear, employee.getId());
        Assertions.assertEquals(types.size(), result.size());
        types.forEach(type -> {
            var limit = result.stream().filter(l -> l.getTimeOffType().equals(type)).findAny();
            Assertions.assertTrue(limit.isPresent());
            Assertions.assertEquals(120, limit.get().getMaxHours());
            Assertions.assertEquals(employee, limit.get().getEmployee());
            Assertions.assertEquals(leaveYear, limit.get().getLeaveYear());
        });
    }

    @Test
    void getById() {
        List.of(Long.MIN_VALUE, -54321L, -1L, 0L).forEach(id -> Assertions.assertThrows(ValidationException.class, () -> timeOffLimitService.getById(id)));
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var limit = DefaultTestObjects.getLimitEntity(type, employee);
        timeOffLimitRepository.save(limit);
        Assertions.assertEquals(limit, timeOffLimitService.getById(limit.getId()));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.getById(limit.getId() == 1L ? 3L : limit.getId() - 1L)).getStatus());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.getById(limit.getId() + 1L)).getStatus());
    }

    @Test
    void putAll_id() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var limit = DefaultTestObjects.getLimitEntity(type, employee);
        timeOffLimitRepository.save(limit);

        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        List.of(Long.MIN_VALUE, -54321L, -1L).forEach(id -> {
            dto.setId(id);
            Assertions.assertThrows(ValidationException.class, () -> timeOffLimitService.putAll(List.of(dto)));
        });

        dto.setId(limit.getId() + 1);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
        dto.setId(limit.getId() == 1 ? 3L : limit.getId() - 1);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
    }

    @Test
    void putAll_maxHours() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        List.of(Integer.MIN_VALUE, -100, -1).forEach(h -> {
            dto.setMaxHours(h);
            Assertions.assertThrows(ValidationException.class, () -> timeOffLimitService.putAll(List.of(dto)));
        });

        for (int y = 2025; y < 2050; y++) {
            dto.setYear(y);
            dto.setMaxHours(LocalDate.of(y, 12, 31).getDayOfYear() * 24 + 1);
            Assertions.assertThrows(jakarta.validation.ValidationException.class, () -> timeOffLimitService.putAll(List.of(dto)));
            dto.setMaxHours(LocalDate.of(y, 12, 31).getDayOfYear() * 24);
            Assertions.assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));
            timeOffLimitRepository.deleteAll();
        }
    }

    @Test
    void putAll_typeIdAndEmployeeId() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);

        List.of(Long.MIN_VALUE, -54321L, -1L, 0L).forEach(id -> {
            {
                var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
                dto.setTypeId(id);
                Assertions.assertThrows(ValidationException.class, () -> timeOffLimitService.putAll(List.of(dto)));
            }
            var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
            dto.setEmployeeId(id);
            Assertions.assertThrows(ValidationException.class, () -> timeOffLimitService.putAll(List.of(dto)));
        });

        {
            var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
            dto.setTypeId(type.getId() + 1);
            Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
        }

        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        dto.setEmployeeId(employee.getId() + 1);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
    }

    @Test
    void putAll_year() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);

        List.of(Integer.MIN_VALUE, -1000, -1, 0, 1, 100, 2000, 2019, 2101, 2222222, Integer.MAX_VALUE).forEach(y -> {
            var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
            dto.setYear(y);
            Assertions.assertThrows(ValidationException.class, () -> timeOffLimitService.putAll(List.of(dto)));
        });
        for (int y = 2020; y <= 2100; y++) {
            var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
            dto.setYear(y);
            Assertions.assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));
            timeOffLimitRepository.deleteAll();
        }
    }

    @Test
    void putAll_newEntityTypeOrEmployeeDoesNotExist() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);

        {
            var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
            dto.setTypeId(type.getId() + 1);
            Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
        }

        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        dto.setEmployeeId(employee.getId() + 1);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
    }

    @Test
    void putAll_newEntityLimitAlreadyExists() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        timeOffLimitService.putAll(List.of(dto));
        dto.setMaxHours(120);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
    }

    @Test
    void putAll_updateNotExistingEntity() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        dto.setId(1);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffLimitService.putAll(List.of(dto))).getStatus());
    }

    @Test
    void putAll_create() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        Assertions.assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));
        Assertions.assertEquals(1, timeOffLimitRepository.count());
        var record = timeOffLimitRepository.findAll().getFirst();
        Assertions.assertEquals(dto.getEmployeeId(), record.getEmployee().getId());
        Assertions.assertEquals(dto.getTypeId(), record.getTimeOffType().getId());
        Assertions.assertEquals(dto.getYear(), record.getLeaveYear());
        Assertions.assertEquals(dto.getMaxHours(), record.getMaxHours());
    }

    @Test
    void putAll_updateChangesOnlyYear() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var limit = DefaultTestObjects.getLimitEntity(type, employee);
        timeOffLimitRepository.save(limit);

        var dto = DefaultTestObjects.getTimeOffLimitPutDto(type.getId(), employee.getId());
        dto.setId(limit.getId());
        dto.setMaxHours(limit.getMaxHours() + 1);
        dto.setYear(limit.getLeaveYear() + 1);
        dto.setTypeId(limit.getTimeOffType().getId() + 1);
        dto.setEmployeeId(limit.getEmployee().getId() + 1);

        Assertions.assertDoesNotThrow(() -> timeOffLimitService.putAll(List.of(dto)));
        Assertions.assertEquals(1, timeOffLimitRepository.count());

        var record = timeOffLimitRepository.findAll().getFirst();
        Assertions.assertEquals(dto.getEmployeeId() - 1, record.getEmployee().getId());
        Assertions.assertEquals(dto.getTypeId() - 1, record.getTimeOffType().getId());
        Assertions.assertEquals(dto.getYear() - 1, record.getLeaveYear());
        Assertions.assertEquals(dto.getMaxHours(), record.getMaxHours());
    }
}
