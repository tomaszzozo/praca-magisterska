package com.tul.tomasz_wojtkiewicz.praca_magisterska.service;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.ApiException;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.DefaultTestObjects;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.EmployeeRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffLimitRepository;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.repository.TimeOffTypeRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

@SpringBootTest
class TimeOffTypeServiceTests {
    @Autowired
    private TimeOffTypeRepository timeOffTypeRepository;
    @Autowired
    private TimeOffTypeService timeOffTypeService;
    @Autowired
    private TimeOffLimitRepository limitRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TimeOffRepository timeOffRepository;

    @AfterEach
    void afterEach() {
        timeOffRepository.deleteAll();
        limitRepository.deleteAll();
        timeOffTypeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void getById() {
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        Assertions.assertEquals(type, timeOffTypeService.getById(type.getId()));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffTypeService.getById(type.getId() + 1L)).getStatus());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, Assertions.assertThrows(ApiException.class, () -> timeOffTypeService.getById(type.getId() == 1 ? 3L : type.getId() - 1L)).getStatus());
    }

    @Test
    void getById_outOfRangeIds() {
        List.of(Long.MIN_VALUE, -54321L, -1L, 0L).forEach(id -> Assertions.assertThrows(ValidationException.class, () -> timeOffTypeService.getById(id)));
    }

    @Test
    void putAll_outOfRangeIds() {
        List.of(Long.MIN_VALUE, -54321L, -1L).forEach(id -> {
            var dto = DefaultTestObjects.getTimeOffTypePutDto();
            dto.setId(id);
            Assertions.assertThrows(ValidationException.class, () -> timeOffTypeService.putAll(List.of(dto)));
        });
    }

    @Test
    void putAll_creatingData() {
        var dto = DefaultTestObjects.getTimeOffTypePutDto();
        dto.setId(0);
        Assertions.assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of(dto)));
        Assertions.assertEquals(1, timeOffTypeRepository.findAll().size());
    }

    @Test
    void putAll_deletingData() {
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        Assertions.assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of()));
        Assertions.assertTrue(timeOffTypeRepository.findAll().isEmpty());
    }

    @Test
    void putAll_updatingData() {
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);

        var dto = DefaultTestObjects.getTimeOffTypePutDto();
        dto.setId(type.getId());
        dto.setName(type.getName()+" #2");
        dto.setCompensationPercentage(type.getCompensationPercentage()*0.5f);
        Assertions.assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of(dto)));
        Assertions.assertEquals(1, timeOffTypeRepository.findAll().size());

        var result = timeOffTypeRepository.findById(type.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(dto.getId(), result.get().getId());
        Assertions.assertEquals(dto.getName(), result.get().getName());
        Assertions.assertEquals(dto.getCompensationPercentage(), result.get().getCompensationPercentage());
    }

    @Test
    void putAll_deletingUsedType() {
        var employee = DefaultTestObjects.getEmployeeEntity();
        employeeRepository.save(employee);
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);
        var limit = DefaultTestObjects.getLimitEntity(type, employee);
        limitRepository.save(limit);
        var timeOff = DefaultTestObjects.getTimeOffEntity(limit);
        timeOffRepository.save(timeOff);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of())).getStatus());
    }

    @Test
    void putAll_nameUniqueness() {
        var dto1 = DefaultTestObjects.getTimeOffTypePutDto();
        var dto2 = DefaultTestObjects.getTimeOffTypePutDto();
        dto2.setName(dto1.getName()+"#2");
        timeOffTypeService.putAll(List.of(dto1, dto2));
        dto1.setId(timeOffTypeRepository.findAll().getFirst().getId());
        dto2.setId(timeOffTypeRepository.findAll().getLast().getId());

        var dto3 = DefaultTestObjects.getTimeOffTypePutDto();
        dto3.setId(0);
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(dto1, dto2, dto3))).getStatus());

        dto2.setName(dto1.getName());
        Assertions.assertEquals(HttpStatus.CONFLICT, Assertions.assertThrows(ApiException.class, () -> timeOffTypeService.putAll(List.of(dto1, dto2))).getStatus());
    }

    @Test
    void putAll_compensationPercentage() {
        var type = DefaultTestObjects.getTimeOffTypeEntity();
        timeOffTypeRepository.save(type);

        List.of(-Float.MAX_VALUE, -123456f, -Float.MIN_VALUE, 100.1f, 123f, 1234f, 12345f, 123456f, Float.MAX_VALUE).forEach(cp -> {
            var dto = DefaultTestObjects.getTimeOffTypePutDto();
            dto.setId(type.getId());
            dto.setCompensationPercentage(cp);
            Assertions.assertThrows(ValidationException.class, () -> timeOffTypeService.putAll(List.of(dto)));
        });
        List.of(0f, 2.23f, 25.86f, 50f, 75.75f, 100f).forEach(cp -> {
            var dto = DefaultTestObjects.getTimeOffTypePutDto();
            dto.setId(type.getId());
            dto.setCompensationPercentage(cp);
            Assertions.assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of(dto)));
        });
    }

    @Test
    void putAll_updateDeleteAndCreateData() {
        var dto1 = DefaultTestObjects.getTimeOffTypePutDto();
        var dto2 = DefaultTestObjects.getTimeOffTypePutDto();
        dto2.setName(dto1.getName()+"#2");
        timeOffTypeService.putAll(List.of(dto1, dto2));
        dto1.setId(timeOffTypeRepository.findAll().getFirst().getId());
        dto2.setId(timeOffTypeRepository.findAll().getLast().getId());
        dto2.setName(dto2.getName()+"2");
        var dto3 = DefaultTestObjects.getTimeOffTypePutDto();
        dto3.setId(0);
        Assertions.assertDoesNotThrow(() -> timeOffTypeService.putAll(List.of(dto2, dto3)));
        Assertions.assertEquals(2, timeOffTypeRepository.findAll().size());
        Assertions.assertEquals(dto2.getId(), timeOffTypeRepository.findAll().getFirst().getId());
        Assertions.assertEquals(dto2.getName(), timeOffTypeRepository.findAll().getFirst().getName());
        Assertions.assertNotEquals(dto1.getId(), timeOffTypeRepository.findAll().getLast().getId());
    }
}
